#! /bin/bash

host=172.31.50.56
postgres_port=5432

port=8769
echo 'fitbit-web-service: waiting for config-service.'
until $(curl --output /dev/null --silent --head --fail http://$host:$port/ping); do
    printf "."
    sleep 3
done
echo "config-service is up"

port=8770
printf "\n"
echo 'fitbit-web-service: waiting for discovery-service.'
until $(curl --output /dev/null --silent --head --fail http://$host:$port/ping); do
    printf "."
    sleep 3
done
echo "discovery-service is up"

printf "\n"
echo 'fitbit-web-service: waiting for postgreSQL.'

/opt/lib/wait-for-it.sh -h $host -p $postgres_port -t 0 --strict -- \
echo "postgreSQL is up"
/usr/bin/java -jar -Dspring.profiles.active=prod /opt/lib/fitbit-web-service-1.0-SNAPSHOT.jar