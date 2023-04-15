
Para Jesús


Hola Jesús, te paso material para el workshop: 
- Ponente: Antonio Mendoza Pérez https://www.linkedin.com/in/antmendoza 
- Título: Creando aplicaciones invencibles con Temporal

- Intro 2 min: https://www.youtube.com/watch?v=f-18XztyN6c
- Demo app: https://www.youtube.com/watch?v=jjRu8GJgL1k





## SLIDE 1

Welcome everyone and thank you for being here. It is a pleasure for me, thank you.

My name is Antonio, I am a Developer Success Engineer at Temporal, and I am gonna be delivering this workshop 
for you today and answering (or trying) any questions you might have. 

BUILDING RELIABLE APPLICATION WITH TEMPORAL


## SLIDE 2

### Agenda:
Workshop (2h)
- **Environment setup**
  - first, i need you to install a couple of components that we are gonna use during the workshop, this is basically 
  Temporal server, clone a repo containing the exersices we are gonna do and from there we can start the workshop.
- **Introduction / What is Temporal?**
  - secondly I am gonna give a small introduction on what is Temporal and go over, just tuching the surface, on how it works. 
    - Main components
    - Main building blocs
  
- **Hands on** 
  - We are gonna introduce a use case, and we are gonna work on it incrementally 
  - money transfer application

- **This is your time, choose a use-case and work on it**
  - Choose a use case
  - Hands on
  - Work in couples 

  
## SLIDE 2
### Introduction / What is Temporal?


What is temporal and why temporal is usufull, talks about retries and stufff 

As we move from monolitics to services, or micro-services, we start introducing complexity in our systems.

- if we have an operation that involves two request, we want this operation to be consistent, either it completes suscessfully or it fail

Example transfer money application, if the second operation fails, you need to start writing code around retries etc...  
Is a workflow engine with super-capabilities like durable  timers, retries, configuration....
Is an orchestration engine that allows you to write your orchestration as code, 
And remove from your code the complexity to deal with timers, retries, transactions... 

Is very difficult to create complex applications if all you have is queues. (Kafka), 

Is a system to orchestration microservices, activity invocations as we will see later. 


 // you need a system to orchrestate this applications, and this is where Temporal take place. Symple workflow service.

Cadence created in 2015 by Samar and Maxim in 2015 while they where in Uber.
In 2019, Temporal is a fork of Cadence,
Samar and Maxim worker in different projects for the last 15 years before create Temporal. Like Amazon SQS, Simple WF or Azure Durable task.

is a system to create durable execution / orchrestate micro services, it comes with 

We are goona see later the money transfer application, a simple use case that shows how powerfull temporal can be. 


Temporal remove all the complexity around Timers, retries, concurrenty, rate limiting request etc... 



Try to introduce one slide that shows the complexity of having system implace like kafka etc..., timers, retries... between temporal

put messages in kafka and the database fails, or the other way around 



## SLIDE 3
### Environment setup

- docker 
- git clone d


- Mac / Linux / Windows 
https://docs.temporal.io/kb/all-the-ways-to-run-a-cluster#temporal-cli


- or temporal cli



Before star writing the application we need to create the environment where this application is gonna live, where is gonna be executed etc.... Persist workflow state etc...
There are three main parts involves in a temporal application.
- Cluster
- SDKs
  - Workflow
  - Client that initiate the request
- Worker




## SLIDE

- 
- Hello world application.
      
- start docker / temporal cli
- the enviroment, open the code and run the starter
- three main components, 
- the client, the server and the worker
  - worker execute the code, workflows and activities 
  - start the worker and see what has happened. 




## SLIDE

#### cluster (server and database) 
- Is responsible for persisting the workflow state, timers etc... which is where workflow lives, is persisted etc.. 
- There are different components. 
  - Timers
  - po....
Why we need task queues? beacuse different workflow wihtin the same namespacew could require different configuration at runtime.Like rate limit, 
resources depending on the heavy load. 
- tasqusues allow ... //TODO 
- is and endpoint within the namesapces. 

Receives a request wich is a command that is transformed to an event in the data base. 
gRPC call, that are wrapped by the API 


We will see the API latter, but basically from the API we can do something like. 

WorkflowClient.start(WorkflowImplementation:execute, "my data")

- command START_WORKFLOW_EXECUTION

 TODO hacer dibujo de command que se envia


There are different component involved, but at the end there is what we call a workflow history and every command attach an event to the workflow history. 


There are different ways right now to setup a local cluster, these are three main two.
- git clone ... docker compose up / `TODO` pedir ayuda Ivan
- brew install temporal &
  - Temporal serverl + command line interface. 


#### Presentation para ellos
- git clone ... docker compose up / `TODO` pedir ayuda Ivan
- brew install temporal &
  - Temporal serverl + command line interface.


Ask them to execute the code. 



## SLIDE 4
#### SDKs
- API to write workflows
  - timer Worklow.timer(())
  - sleep Workflow.sleep
  - Async.
- API to query workflows 
- Different SDKs, Java, Python, Go, Typescript, .Net, Rush.. 

the SDK is gonna allow us to create: 
- clients

  We have seen a little example before, but 
     - workflow.start.. is an example of client. 
     - Workflow.signal is another example. 



- write workflow code

  - Workflow.sleep (Duration.ofMinutes(30))

- create workers
 //TODO





## SLIDE 
#### Workers
- Responsible for running your code. The server is reponnsible for creating persis the workflow execution

Show code about workers

Or imagine that in the middle of an operation the process fail, it will recover your process in the middle
If the worker crash your temporal will recover the state of the program in a different machine , the state or objects, workflow variables etc... thread.. data in your clasess

When you write code you don't need to care for the failure of the code

process with be recover in other maching a

The main value proposition of temporal is, you write normal code, and this code is fault tolerance



## SLIDE

Recap:
- something that initiate a request in


## How this works

gRPC


Worker open a long request asking for things to do, either workflow or activity task in an specific taskqueue.







think that this can be a long running workflow or short runing workflow like seconds 




### Randon notes: 

Consistency: 
- is executed only 

Determinism: 
- for the same imput have to produce the same output. 
- workflow replyer
- We can not do UUID.rtandom, we have to do instead Workflow.randomID()
- A workflow workflow type, workflow id, and run id or execution id
  - Workflow type is the workflow implementation.
  - Workflow id is a bussines name, we can set the workflow id.
  - Run id is unique by namespace and is set by the server.


- Concept: Data converters, 

- You can build your services in different languajes as long as they follow the same contract. 

Resources: 
Versioning: 
- https://www.youtube.com/watch?v=kkP899WxgzY
- https://community.temporal.io/t/workflow-versioning-strategies/6911

Timeouts: https://www.youtube.com/watch?v=JK7WLK3ZSu8


Debug: 
- Testing: 
- Reply workflow: 

Java Workshops: 
- https://www.youtube.com/watch?v=VoSiIwkvuX0 (Part 1)
- https://www.youtube.com/watch?v=h-TSDMULCf0 (Part 2)
- https://www.youtube.com/watch?v=8DFox0fGjzI (Part 3)
- https://www.youtube.com/watch?v=v-s-Umt1Q0A (Part 4)
- https://www.youtube.com/watch?v=KShybZOMo-8 (Part 5)




- Workflows
- Activities 
- Child workflows
- Sync / Async
- Threads in Java.
