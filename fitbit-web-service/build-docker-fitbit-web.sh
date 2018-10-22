#!/usr/bin/env bash

mvn package spring-boot:repackage -DskipTests
printf "\nCOMPILED FITBIT-WEB-SERVER"

docker build --file=Dockerfile-fitbit-web --tag=nsimsiri/fitbit-web-service:latest --rm=true .
printf "\nBUILT docker\n"

docker push nsimsiri/fitbit-web-service:latest
printf "\nPUSH image to repo\n"

#printf "\nRUNNING..."
#docker run --rm=true --name=fitbit-web-service --publish=8771:8771 nsimsiri/fitbit-web-service:latest
