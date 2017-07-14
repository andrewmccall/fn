# fn
fn (pronounced fun) is a serverless framework for developing synchronous and asynchronous functional services and applications.

![Build Status](https://travis-ci.org/andrewmccall/fn.svg?branch=master)

#Components

## Invoker

The Invoker is the agent which executes user functions. 

## Gateway

Stateless component that accepts HTTP traffic and forward to invokers.

## Controller

The controller is the interface and abstraction of the underlying resource schedulers. 
The controller handles autoscaling and failover of executing functions.

