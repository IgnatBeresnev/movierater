## Решение в лоб
Можно было забить на все то, что описано ниже, и на каждый запрос средней оценки по жанру раз нагибать сервак на 2000+ rps,
на лету суммируя average_vote, и в конце выплюнуть результат. Делается это все с ForkJoinPool
за пару часов максимум. Но я подумал, что это не совсем то, что хотелось бы увидеть на тестовом задании (хоть это и решает
поставленную задачу), поэтому...

## Энтерпрайз решение

### Стек
* Java 11 (из-за встроенного [HttpClient](http://openjdk.java.net/jeps/321)), Gradle 5.1
* Spring 5 (только context), Lombok, Jackson, JUnit 5
* ChronicleMap

### Почему ChronicleMap
Сразу стало понятно, что надо кэшировать данные. На каждый запрос бегать по АПИ не очень оптимально:
1. Чтобы было быстро, нужно распараллеливать, для этого нужно CPU
2. Если распараллеливать, то нужно чтобы АПИ держало большое количество rps
3. Если сервисом будут пользователься несколько пользователей одновременно, даже с учетом п.1 и п.2,
им придется ждать своей очереди.
4. По сути не скейлится. Это щас в апи 20 тыс фильмов, а завтра будет синхронизация с IMDB и станет 5 миллионов

Также стояло условие персистентности, чтобы данные лежали на диске и при каждом стартапе не приходилось
заполнять кэш данными, и хотелось embedded решение, чтобы не нужно было ставить никакие серваки.

Из рассмотренных вариантов
* HashMap - не хочется раздувать хип (именно jvm хип)
* MongoDB. Хранение сырых json, которые возвращаются с апи. Чтобы показывать прогресс подсчета - сделать пагинацию с 
incremental mapReduce, который будет подсчитывать среднюю оценку. Вариант хороший, но учитывая ограничение по
времени - слишком муторный.
* Hazelcast/etc - нет нормального хранения на диске, хочется персистетность, чтобы при каждом стартапе не приходилось заполнять кэш данными
* Redis/etc - не хочется разворачивать дополнительный сервер

ChronicleMap
* Embedded
* Хорошая concurrency, low latency
* Персистентность
* Использует оффхип / mmap
* Хорошие [показатели бенчмарков](https://jetbrains.github.io/xodus/) на итерацию (то что нам надо)

### Архитектура
Основная идея - репозитории (например, [ChronicleMapMovieRepository](https://github.com/IgnatBeresnev/movierater/blob/master/src/main/java/ru/loaltyplant/movierater/repository/movie/ChronicleMapMovieRepository.java))
отвечают только за хранение данных и ничего не знают про API. Синхронизацией данных АПИ с репозиторием
занимается отдельный сервис (например, [LoaltyplantApiMovieRepositorySyncService](https://github.com/IgnatBeresnev/movierater/blob/master/src/main/java/ru/loaltyplant/movierater/service/sync/LoaltyplantApiMovieRepositorySyncService.java)).
Подсчет среднего ведется в репозитории. Да, данные в кэше не всегда свежие, но от 10 секундной задержки синхронизации хуже не станет.

Все легко заменяемо: можно сделать `RedisMovieRepository` или `ImdbMovieRepositorySyncService` и будет нормально работать.

Условие с отменой подсчета и получением прогресса реализована через [DelegateProgressableFuture](https://github.com/IgnatBeresnev/movierater/blob/master/src/main/java/ru/loaltyplant/movierater/concurrent/DelegateProgressableFuture.java).
Имплементит `Future<?>`, делегируя вызовы тому, что мы получили при сабмите в executor (чтобы можно было вызывать cancel), и содержит
счетчик прогресса [ProgressCounter](https://github.com/IgnatBeresnev/movierater/blob/master/src/main/java/ru/loaltyplant/movierater/concurrent/ProgressCounter.java)

Проперти разбиты тематически в отдельные классы, монолитный конфиг при желании можно быстро разнести. Очень бы
хотелось использовать [ConfigurationProperties](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/context/properties/ConfigurationProperties.html),
но ради этого одного тащить бут не вариант.
