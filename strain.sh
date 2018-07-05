#!/bin/bash

if [ $1 = "1" ]
then
  cmd="mvn spring-boot:run -pl config-service"
  echo $cmd
  eval $cmd;
fi

if [ $1 = "2" ]
then
  cmd="mvn spring-boot:run -pl discovery-service"
  echo $cmd
  eval $cmd;
fi

if [ $1 = "3" ]
then
  cmd="mvn spring-boot:run -pl api-gateway-service"
  echo $cmd
  eval $cmd;
fi


if [ $1 = "infra" ]
then
  # mvn spring-boot:run -pl zuul-service
  mvn spring-boot:run -pl config-service > conf-log.txt &
  export a=$!
  mvn spring-boot:run -pl discovery-service
  echo "killed"
  echo $a;
fi
#
# if [ $1 = "service" ]
# then
#   # mvn spring-boot:run -pl fitbit-web-service > conf-log.txt
# fi

if [ $1 = "fitbit-web-service" ]
then
  cmd="mvn spring-boot:run -pl fitbit-web-service"
  echo $cmd;
  eval $cmd;
fi

if [ $1 = "account-service" ]
then
  cmd="mvn spring-boot:run -pl account-service"
  echo $cmd;
  eval $cmd;
fi
