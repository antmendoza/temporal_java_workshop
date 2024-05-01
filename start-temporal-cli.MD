# RUN MANUALLY THE STEPS IN THE SCRIPT 

- Start the server

```bash
temporal server start-dev \
--dynamic-config-value frontend.enableUpdateWorkflowExecution=true \
--db-filename "my_test.db" \
--ui-port "8080"

```

The previous command persist the data in the filesystem. You can clean the environment by deleting the file (and restart the server after).

- Wait until the server is up. You navigate to http://localhost:8080/ to verify it.

- Add the Search attribute `TransferRequestStatus` (this is only needed for the `_final` exercise)

```bash
temporal operator search-attribute create --namespace "default" \
        --name TransferRequestStatus --type Keyword
```

