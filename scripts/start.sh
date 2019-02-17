#!/bin/bash

jdkPath=./jdk-11.0.1
jarFile=./movierater-1.0.jar

applicationProperties=./application.properties
logsCommonPath=./common.log

${jdkPath}/bin/java -DapplicationProperties=${applicationProperties} -Dlogs.common.path=${logsCommonPath} -jar ${jarFile}
