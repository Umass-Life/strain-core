#!/usr/bin/env bash
docker run --name strain-postgres \
-p 5432:5432 \
-e POSTGRES_PASSWORD=1234 \
-e POSTGRES_USER=strain \
-e POSTGRES_DB=strain \
-d postgres:alpine