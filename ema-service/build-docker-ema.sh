#!/usr/bin/env bash

mvn package spring-boot:repackage -DskipTests
printf "\nCOMPILED EMA-SERVER"

docker build --file=Dockerfile-ema --tag=nsimsiri/ema-service:latest --rm=true .
printf "\nBUILT docker\n"

docker push nsimsiri/ema-service:latest
printf "\nPUSH image to repo\n"

#printf "\nRUNNING..."
#docker run --rm=true --name=account-service --publish=8772:8772 nsimsiri/account-service:latest
