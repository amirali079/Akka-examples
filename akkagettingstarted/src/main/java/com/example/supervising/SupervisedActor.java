package com.example.supervising;

import akka.actor.typed.Behavior;
import akka.actor.typed.PostStop;
import akka.actor.typed.PreRestart;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class SupervisedActor extends AbstractBehavior<String> {

    static String state = "1";

    private SupervisedActor(ActorContext<String> context) {
        super(context);
        System.out.println("supervised " + getContext().getSelf() + " started " + " --in " + Thread.currentThread() + "   --state: " + state);


    }

    static Behavior<String> create() {
        return Behaviors.setup(SupervisedActor::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("fail", this::fail)
                .onSignal(PreRestart.class, signal -> preRestart())
                .onSignal(PostStop.class, signal -> postStop())
                .build();
    }

    private Behavior<String> fail() {
        System.out.println("supervised " + getContext().getSelf() + " fails now " + " --in " + Thread.currentThread());
        throw new RuntimeException("I failed!");
    }

    private Behavior<String> preRestart() {
        state = "2";
        System.out.println("supervised " + getContext().getSelf() + "  will be restarted " + " --in " + Thread.currentThread());
        return this;
    }

    private Behavior<String> postStop() {
        System.out.println("supervised " + getContext().getSelf() + "  stopped " + " --in " + Thread.currentThread());
        return this;
    }
}