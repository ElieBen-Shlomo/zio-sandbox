#!/usr/bin/env bash

function log {
    echo "[$(date)]: $*"
}

function waitForClusterConnection() {
    container_name=$1

    log "Waiting for Cassandra connection..."
    retryCount=0
    maxRetry=20
    docker exec $container_name cqlsh -e "Describe KEYSPACES;" &>/dev/null
    while [ $? -ne 0 ] && [ "$retryCount" -ne "$maxRetry" ]; do
        log 'Cassandra not reachable yet. sleep and retry. retryCount =' $retryCount
        sleep 5
        ((retryCount+=1))
        docker exec $container_name cqlsh -e "Describe KEYSPACES;" &>/dev/null
    done

    if [ $? -ne 0 ]; then
      log "Not connected after " $retryCount " retry. Abort the migration."
      exit 1
    fi

    log "Connected to Cassandra cluster"
}

function executeScripts() {
    local container_name=$1
    local file_pattern=$2
    for cql_file in $file_pattern; do
        file_name=$(basename $cql_file)
        log "Executing cql from $file_name"
        docker cp $cql_file $container_name:/var/tmp
        docker exec $container_name cqlsh -f /var/tmp/$file_name
    done
}

container_name=cassandra_db
docker run -d -p 9042:9042 --name $container_name cassandra
waitForClusterConnection $container_name
executeScripts $container_name "database/evolutions/*.cql"

sbt run