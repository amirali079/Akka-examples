package com.example.print;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class Main extends AbstractBehavior<String> {

    private Main(ActorContext<String> context) {
        super(context);
    }

    static Behavior<String> create() {
        return Behaviors.setup(Main::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("startFirstExample", this::start)
                .build();
    }

    private Behavior<String> start() {
        System.out.println("User Ref: " + getContext().getSelf());
        System.out.println("User path  : " + getContext().getSelf().path());

        ActorRef<String> firstRef = getContext().spawn(PrintMyActorRefActor.create(), "first-actor");
        System.out.println("First Ref: " + firstRef);
        System.out.println("First path: " + firstRef.path());

        firstRef.tell("printit");

        return Behaviors.same();
    }


}