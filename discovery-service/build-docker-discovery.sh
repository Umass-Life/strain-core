#!/usr/bin/env bash

# script to build/run just discovery service.

mvn package spring-boot:repackage
printf "\nCOMPILED DISCOVERY-SERVER"

docker build --file=Dockerfile-discovery --tag=nsimsiri/discovery-service:latest --rm=true .
printf "\nBUILT docker\n"

docker push nsimsiri/discovery-service:latest
printf "\nPUSH image to repo\n"

#printf "\nRUNNING..."
#docker run --rm=true --name=discovery-service --publish=8770:8770 nsimsiri/discovery-service:latest

