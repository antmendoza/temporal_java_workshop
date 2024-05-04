#!/bin/bash

# If the script does not work for see "start-temporal-cli.md"

: "${DEFAULT_NAMESPACE:=default}"

: "${FILE_DB:=my_test.db}"

# TODO add delete DB


add_custom_search_attributes() {
    until temporal operator search-attribute list --namespace "${DEFAULT_NAMESPACE}"; do
      echo "Waiting for namespace..."
      sleep 1
    done

    echo "Adding Custom  search attributes, only required for the final exercises "

    temporal operator search-attribute create --namespace "${DEFAULT_NAMESPACE}" \
        --name TransferRequestStatus --type Keyword

}

setup_server(){

    sleep 2

    add_custom_search_attributes

    sleep 2

    echo "------"
    echo ">>> Temporal UI >>>  http://localhost:8080/"
    echo "------"


}

# Run this func in parallel process. It will wait for server to start and then run required steps.
setup_server &

temporal server start-dev \
--dynamic-config-value frontend.enableUpdateWorkflowExecution=true \
--ui-port "8080" \
#--db-filename "${FILE_DB}" \
