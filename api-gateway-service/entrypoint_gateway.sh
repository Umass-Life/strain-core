#!/bin/ash

host=172.31.50.56

port=8769
echo 'gateway-service: waiting for config-service.'
echo $host
until $(curl --output /dev/null --silent --head --fail http://$host:$port/ping); do
    printf "."
    sleep 3
done

port=8770
echo 'gateway-service: waiting for discovery-service.'
until $(curl --output /dev/null --silent --head --fail http://$host:$port/ping); do
    printf "."
    sleep 3
done

/usr/bin/java -jar -Dspring.profiles.active=prod /opt/lib/api-gateway-service-1.0-SNAPSHOT.jar