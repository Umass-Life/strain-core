#!/usr/bin/env bash

mvn package spring-boot:repackage
printf "\nCOMPILED GATEWAY-SERVER"

docker build --file=Dockerfile-gateway --tag=nsimsiri/gateway-service:latest --rm=true .
printf "\nBUILT docker\n"

docker push nsimsiri/gateway-service:latest
printf "\nPUSH image to repo\n"

#printf "\nRUNNING..."
#docker run --rm=true --name=gateway-service --publish=8768:8768 nsimsiri/gateway-service:latest
