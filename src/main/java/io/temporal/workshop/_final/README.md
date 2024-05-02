# WIP

### Run the code 


####  Run the code

- Ensure you have everything you need to run the code, and the Temporal Server is running.
  See [prepare-your-environment.md](./../../../../../../../../prepare-your-environment.md).

- Stop any processes that are running as part of other exercises (such as workers), except the Temporal Server.

- Start the Worker process

```bash
# Go to the root directory
cd ./../../../../../../../
# from the root directory execute
 ./mvnw compile exec:java -Dexec.mainClass="io.temporal.workshop._final.WorkerProcess"

```

- Start the Spring Boot application

```bash
# Go to the root directory
cd ./../../../../../../../
# from the root directory execute
 ./mvnw compile exec:java -Dexec.mainClass="io.temporal.workshop._final.springrunner.Application"

```

Navigate to  [http://localhost:3030/](http://localhost:3030/), where the application exposes the UI.
It has links `(View in Temporal UI)` to help relate what is shown in this UI with the Temporal UI. 

![img.png](img.png)