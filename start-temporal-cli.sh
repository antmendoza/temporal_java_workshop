#!/bin/bash



file="my_test.db"

if [ -f "$file" ] ; then

    rm "$file"
fi


: "${DEFAULT_NAMESPACE:=default}"

add_custom_search_attributes() {
    until temporal operator search-attribute list --namespace "${DEFAULT_NAMESPACE}"; do
      echo "Waiting for namespace cache to refresh..."
      sleep 1
    done

    echo "Adding Custom  search attributes."

    temporal operator search-attribute create --namespace "${DEFAULT_NAMESPACE}" \
        --name TransferRequestStatus --type Keyword
}

setup_server(){

    sleep 2

    add_custom_search_attributes
    echo ">>> UI running in http://localhost:8233/"
    echo ">>> UI running in http://localhost:8233/"
    echo ">>> UI running in http://localhost:8233/"
    echo ">>> UI running in http://localhost:8233/"
}

# Run this func in parallel process. It will wait for server to start and then run required steps.
setup_server &

temporal server start-dev --dynamic-config-value frontend.enableUpdateWorkflowExecution=true \
--db-filename $file
