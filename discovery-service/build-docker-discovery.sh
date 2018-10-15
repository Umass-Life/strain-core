#!/usr/bin/env bash

mvn package spring-boot:repackage
printf "\nCOMPILED DISCOVERY-SERVER"

docker build --file=Dockerfile-discovery --tag=discovery-service:latest --rm=true .
printf "\nBUILT docker\n"

printf "\nRUNNING..."
docker run --rm=true --name=discovery-service --publish=8770:8770 discovery-service:latest
