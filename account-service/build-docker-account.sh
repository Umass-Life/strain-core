#!/usr/bin/env bash

cp -r ../secrets ./

mvn package spring-boot:repackage -DskipTests
printf "\nCOMPILED ACCOUNT-SERVER"

docker build --file=Dockerfile-account --tag=nsimsiri/account-service:latest --rm=true .
printf "\nBUILT docker\n"

docker push nsimsiri/account-service:latest
printf "\nPUSH image to repo\n"

#printf "\nRUNNING..."
#docker run --rm=true --name=account-service --publish=8772:8772 nsimsiri/account-service:latest
