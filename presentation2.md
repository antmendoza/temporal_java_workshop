
## Intro

Welcome everyone, and thank you for being here. 

My name is Antonio, I am a Developer Success Engineer at Temporal, and I am gonna be delivering this workshop

BUILDING RELIABLE APPLICATION WITH TEMPORAL

I have tried to create a very practical workshop, the idea is to having you all working more than me, today, so let's start. 

## Agenda:
Workshop (2h)
- **Introduction (5-10 minutes)**
    - firstly, I am gonna give a small introduction of what  and why Temporal is a great (I would say the best) option 
  to consider when we have to write our applications 

- **Environment setup (5-10 minutes)**
    - secondly, we are gonna prepare our environment. 
We will need to clone a repository and donwload the Temporal server.

- **Practical exercise (30 minutes)**
    - thirdly, We are gonna introduce our use-case and iterate over it introducing new concepts on each step.

- **Break (5 minutes)**
    - we are gonna do a small break

- **Practical exercise (continuation)**
    - and then, continue with the practical exercise

**Play with it**
  - finally, I am gonna ask you to pick a partner, pick one of the exercises and implement it


--- 

## Introduction 

Why Temporal? 

Our applications usually involve either service/microservice orchestration, invoking upstream services, sometimes we have to write pipelines... 
And all have one thing in common, we want transactional operations, we have to be sure that either or nothing completes.


A typical example is a money transfer application: 

```

    public String transfer(TransferRequest transferRequest) {

        accountService.withdraw(fromAccountId, referenceId, amount);
        accountService.deposit(toAccountId, referenceId, amount);

        return "some-transference-id";
    }

```







and wait for the response and with
this kind of interactions in place we have to think about or The way we build applications nowdays is either We in our code or applications have always either micro


Temporal is a system that will allows as to run fault tolerance workflows and in and durable execution

Eliminating complex error or retry logic, avoid callbacks, and ensure that every workflow you start, completes.

Workflow as code. You have to write code to implement your workflows

And application usually is composed by one or more workflows. (workflows comunicate between each other through signals)
Code based Is a code


This is a small introduction on what is temporal and why


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

We are gonna see later the money transfer application, a simple use case that shows how powerfull temporal can be.

Temporal remove all the complexity around Timers, retries, concurrenty, rate limiting request etc...


Try to introduce one slide that shows the complexity of having system implace like kafka etc..., timers, retries... between temporal

put messages in kafka and the database fails, or the other way around



## SLIDE
### Environment setup

Before star writing the application we need to create the environment where this application is gonna live, where is gonna be executed etc.... Persist workflow state etc...
There are three main parts involves in a temporal application.


Show picture on what we are gonna isntall

- [environment setup](./env-setup.md)

Show picture on what we are gonna get / client / worker

- clone https://github.com/antmendoza/jonthebeach_workshop.git


## SLIDE
### Environment setup

Before star writing the application we need to create the environment where this application is gonna live, where is gonna be executed etc.... Persist workflow state etc...
There are three main parts involves in a temporal application.


Show piMostrar figura de lo que vamos a instalar, y hacer

- [environment setup](./env-setup.md)



- clone




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
### lines of code

I have talked in the previous slide abaut Temporal taking care of error handling, retries etc...

In the temporal main page there is an example of code written to handle errors, retries etc... (almost 250 lines)

vs

how it would be if it is written using Temporal API (100 lines)

This has an impact of course in productivity, maintainability and also in the presence or absence of error (the more code we write most likely we will be introducing error)


https://temporal.io/




## SLIDE

#### cluster (server and database)
- Is responsible for persisting the workflow state, timers etc... which is where workflow lives, is persisted etc..
- There are different components.
    - Timers
    - po....
      Why we need task queues? beacuse different workflow wihtin the same namespacew could require different configuration at runtime.Like rate limit,
      resources depending on the heavy load.
- taskqueue allow ... //TODO
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




## Play with it

      - Choose a use case
      - Groups of two/three devs. (I don't wanna see anyone alone)



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


https://community.temporal.io/t/where-does-the-workflow-actually-execute/7663/3

https://temporal.io/use-cases



https://docs.google.com/presentation/d/1IVerAsGl1aeQ25NdpfmPDabpA7-c_BoS3I5g1g7s_5w/edit#slide=id.g18645205848_0_486

