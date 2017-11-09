# fn
fn (pronounced fun) is a serverless framework for developing synchronous and asynchronous functional services and applications.

[![Build Status](https://travis-ci.org/andrewmccall/fn.svg?branch=master)](https://travis-ci.org/andrewmccall/fn) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/c3996eb4ed3f405f990bc9d2a143532e)](https://www.codacy.com/app/andrew_31/fn?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=andrewmccall/fn&amp;utm_campaign=Badge_Grade)

# Components

## Invoker

The Invoker is the agent which executes user functions. 

## Gateway

Stateless component that accepts HTTP traffic and forward to invokers.

## Controller

The controller is the interface and abstraction of the underlying resource schedulers. 
The controller handles autoscaling and failover of executing functions.

