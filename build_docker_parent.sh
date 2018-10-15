#!/usr/bin/env bash
docker build --tag=strain-alpine-jdk:base --rm=true .

docker run --name some-postgres -e POSTGRES_PASSWORD=1234 -d postgres