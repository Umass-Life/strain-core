#!/usr/bin/env bash

docker build --tag=strain-alpine-jdk:base --rm=true .

docker push strain-alpine-jdk:latest
printf "\nPUSH image to repo\n"