# CLOUDPAY CHALLENGE

##INTRODUCTION

I have forked the repository in order to evaluate the current solution and propose a better solution for the platform.

On the other hand, the project is going to be still developed in Java, as the challenge accepts Java or kotlin. I could develop also in kotlin because I have been developing in this language for making Android apps.

I have also realized that the project has not provided any unit test neither any integration test, so the first step is to install all plugins and dependencies needed in order to provide a better tested code.

##INSTALATION OF PLUGINS AND DEPENDENCIES

We use jacoco maven plugin in order to see the coverage of the unit testing realised. We are going to use junit with mockito and jupiter in other to perform our unit testing.

We are going to specify the hyphotesis of our test in order to iterate all over the solution to get the right solution.

We have also installed the lombok library in order to get rid of boilerplate code in Java.



##COMMENTS

As the swagger plugin is installed we can access to our swagger documentation from http://localhost:7070/swagger-ui.html and import it to Postman in order to test the collection with it.

The user maxWithdrawalAmount field is not used anywhere, which should be a test that should be done, out of the scope of this challenge.



##CRITICAL CHANGES

Controllers must be a component that just receive and send data. There should not be any process inside the Controller. 

In order to be more testeable, it would be great to have the params inserted on the parameters of function and not into the request.

In order to change that we must refactor this processing into a service.

##DECISIONS MADE

We are going to use RabbitMQ as a broker of messages between the services as it is one of the most used async queue frameworks. There would be also another possibilites as Kafka or Amazon AWS Simple Queue.

##REFACTORING OF CODE

First of all, we have introduced lombok annotations in some classes and refactor the injection of clases of the controllers by using constructors instead of @Autowired beans. This way, it would be easier to test the Controllers of the class.

## TDD

We are going to build our test at the first stage of the development. The aim of the challenge is solving this issue:
> We noticed that in current solution we are losing some outgoing events about withdrawals. We MUST 100% notify listeners regarding any withdrawal statuses. That means a new solution should be designed to cover the requirement. For example a withdrawal has been sent to provider, we updated a status to processing in database, and then we have to send a notification. What if the notification was failed to send (e.q. connection issues to a messaging provider)?

TDD is based on writting a failing test, writting code in order to pass the test, and then iterate over it to get the better real working solution.

*Requeriment*: We MUST 100% notify listeners regarding any withdrawal statuses 

*Scenario*: 
    When: all the systems goes fine
    Then: all the process is going to be completed, status would be saved and notification sent.
    
*Scenario*:
    When: user request widthdrawal and notification service is down
    Then: user must receive the response to his request and notification has to be queued to be consumed later.
    
    
