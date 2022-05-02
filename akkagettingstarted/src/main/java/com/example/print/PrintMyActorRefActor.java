package com.example.print;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class PrintMyActorRefActor extends AbstractBehavior<String> {

    private PrintMyActorRefActor(ActorContext<String> context) {
        super(context);
    }

    static Behavior<String> create() {
        return Behaviors.setup(PrintMyActorRefActor::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onMessageEquals("printit", this::printIt).build();
    }

    private Behavior<String> printIt() {
        System.out.println("First Ref by self : " + getContext().getSelf());

        ActorRef<String> secondRef = getContext().spawn(Behaviors.empty(), "second-actor");
        System.out.println("Second Ref: " + secondRef);
        System.out.println("Second Path: " + secondRef.path());


        ActorRef<String> thirdRef = getContext().spawn(Behaviors.empty(), "third-actor");
        System.out.println("third Ref: " + thirdRef);
        System.out.println("third Path: " + thirdRef.path());
        return this;
    }
}