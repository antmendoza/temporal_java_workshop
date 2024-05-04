# Run manually the steps in the script 

This repository provides a script [start-temporal-cli.sh](start-temporal-cli.sh) to run temporal cli with the needed 
configuration for this workshop. 

This document describe the steps you will have to follow if for some reason the script does not work for you.


- Ensure you have [temporal cli installed](https://docs.temporal.io/cli#install)

- Start the server

```bash
temporal server start-dev \
--dynamic-config-value frontend.enableUpdateWorkflowExecution=true \
--ui-port "8080" \
#--db-filename "my_test.db" \
```

The previous command persist the data in the filesystem (in a file called my_test.db). To clean 
the environment just delete the file and restart the server.


- Wait until the server is up. You can navigate to [http://localhost:8080/](http://localhost:8080/) to verify it.

- Add the Search attribute `TransferRequestStatus` (this is only needed for the `_final` exercise)

```bash
temporal operator search-attribute create --namespace "default" \
        --name TransferRequestStatus --type Keyword
```

