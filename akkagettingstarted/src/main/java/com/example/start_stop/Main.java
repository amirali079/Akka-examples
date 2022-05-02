package com.example.start_stop;

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
                .onMessageEquals("startSecondExample", this::start)
                .build();
    }


    private Behavior<String> start() {

        ActorRef<String> first = getContext().spawn(StartStopActor1.create(), "first");
        first.tell("stop");

        return Behaviors.same();
    }


}