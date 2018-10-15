#!/usr/bin/env bash

mvn package spring-boot:repackage
printf "\nCOMPILED CONFIG-SERVER"

rm -rf strain-config
cp -r ../../strain-config ./
printf "\nCOPIED ../../strain-config\n"

docker build --file=Dockerfile-config --tag=config-service:latest --rm=true .
printf "\nBUILT docker\n"

printf "\nRUNNING..."
docker run --rm=true --name=config-service --publish=8769:8769 config-service:latest
