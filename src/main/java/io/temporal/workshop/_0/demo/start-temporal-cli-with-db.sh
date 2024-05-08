#!/bin/bash

# If the script does not work for you, you can follow the instructions in "start-temporal-cli.md"

: "${FILE_DB:=my_test.db}"

temporal server start-dev \
--dynamic-config-value frontend.enableUpdateWorkflowExecution=true \
--ui-port "8080" \
--db-filename "${FILE_DB}" \
