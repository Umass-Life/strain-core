#!/usr/bin/env bash
host="$1"
port="$2"
echo'waiting for config-service.'
until $(curl --output /dev/null --silent --head --fail http://$host:$port/ping); do
    printf "."
    sleep 5
done
echo "done"