# CLOUDPAY CHALLENGE

##INTRODUCTION

I have forked the repository in order to evaluate the current solution and propose a better solution for the platform.

On the other hand, the project is going to be still developed in Java, as the challenge accepts Java or kotlin. I could develop also in kotlin because I have been developing in this language for making Android apps.

I have also realized that the project has not provided any unit test neither any integration test, so the first step is to install all plugins and dependencies needed in order to provide a better tested code.

##INSTALATION OF PLUGINS AND DEPENDENCIES

I use jacoco maven plugin in order to see the coverage of the unit testing realised. I am going to use junit with mockito and jupiter in other to perform our testing.

I am going to specify the hypothesis of our test in order to iterate all over the solution to get the right solution.

I have also installed the lombok library in order to get rid of boilerplate code in Java.


##COMMENTS

As the swagger plugin is installed we can access to our swagger documentation from http://localhost:7070/swagger-ui.html and import it to Postman in order to test the collection with it.

The user maxWithdrawalAmount field is not used anywhere, which should be a test that should be done. This check has been introduced on WithdrawalController.


##CRITICAL CHANGES

Controllers must be a component that just receive and send data. There should not be any process inside the Controller. 

In order to be more testable, it would be great to have the params inserted on the parameters of function and not into the request, so I have refactored this processing into a service.

I have introduced a new Search on the WithdrawalScheduleRepository in order to find pending withdrawals. The current process was enqueueing all the time processed withdrawals.

##REFACTORING OF CODE

First of all, I have introduced lombok annotations in some classes and refactor the injection of classes of the controllers by using constructors instead of @Autowired beans. This way, it would be easier to test the Controllers of the class.

I have introduced a new Service to separate Withdrawal and WithdrawalScheduled processes. I also have changed the WithdrawallController in order to be in charge of just receiving params, checking them and then sending the response.


##DECISIONS MADE

I am going to use RabbitMQ as a broker of messages between the services as it is one of the most used async queue frameworks. There would be also another possibilities as Kafka or Amazon AWS Simple Queue.

Using RabbitMQ we can assure 100% of the notifications would be treated by the Consumer of the created Queue. If Notification Service is down, RabbitMQ will queue all messages until one consumer process them.

On the other hand, if RabbitMQ service is down, the withdrawal will be enqueued in a memory queue. The good solution would be to persist the state of the notification in the DB. That way if the service is rebooted, you could also send 100% of the notifications. I have not done it, because I think it is not the aim of this challenge.

##SOLID PRINCIPLES
Following the SOLID principles, We have to decouple all the logic of our service in order to maintain the cleanability and the maintainability of the source code. I have created interfaces and use injection in our project in order to decouple the software and maintain the single responsibility principle. Each class created has just one responsibility.

However, there are some improvements to be made on the code in order to follow the SOLID principles. One of them could be creating interfaces with all the contracts of the services as I have done in the PaymentProvider interface. This way all the providers could be change with another who implements its contract.

##TDD

We are going to build our test at the first stage of the development. The aim of the challenge is solving this issue:
> We noticed that in current solution we are losing some outgoing events about withdrawals. We MUST 100% notify listeners regarding any withdrawal statuses. That means a new solution should be designed to cover the requirement. For example a withdrawal has been sent to provider, we updated a status to processing in database, and then we have to send a notification. What if the notification was failed to send (e.q. connection issues to a messaging provider)?

TDD is based on writing a failing test, writing code in order to pass the test, and then iterate over it to get the better real working solution.

*Requirement*: We MUST 100% notify listeners regarding any withdrawal statuses 

*Scenario*: 
    When: all the systems goes fine
    Then: all the process is going to be completed, status would be saved and notification sent.

*Scenario*:
    When: user request withdrawal and notification service is down
    Then: user must receive the response to his request and notification has to be queued to be consumed later.

*Scenario*:
   When: user request withdrawal and rabbitmq service is down
   Then: user must receive the response to his request and withdrawal has to be queued in order to be sent later to RabbitMQ.

##RABBIT-MQ

Rabbit-mq has been configured to have 2 queues, one for transaction status and the other one for Notifications when a withdrawal changes its status. 

PaymentProvider could be an external service or microservice, but for the challenge I have provided a package with all its entities and services. It could be isolated in another microservice if it would be needed.

I had to think what happens when RabbitMQ is down, that's why there is an internal queue where the DTO of the withdrawal are enqueued in order to be sent to RabbitMQ when it is up again.

Admin console of RabbitMQ could be accessed on http://localhost:15672/. Here we could see the Queues status.

##DOCKER

There is a build.sh file in the project in order to create the docker image and put all services up. The port exposed of the service would be 8080 in case of using the docker-compose.yml

