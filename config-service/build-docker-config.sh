#!/usr/bin/env bash

mvn package spring-boot:repackage
printf "\nCOMPILED CONFIG-SERVER"

rm -rf strain-config
cp -r ../../strain-config ./
printf "\nCOPIED ../../strain-config\n"

docker build --file=Dockerfile-config --tag=nsimsiri/config-service:latest --rm=true .
printf "\nBUILT docker\n"

docker push nsimsiri/config-service:latest
printf "\nPUSH image to repo\n"

#printf "\nRUNNING..."
#docker run --rm=true --name=config-service --publish=8769:8769 nsimsiri/config-service:latest
