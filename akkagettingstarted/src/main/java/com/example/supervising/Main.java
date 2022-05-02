package com.example.supervising;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.Props;
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
                .onMessageEquals("startThirdExample", this::start)
                .build();
    }


    private Behavior<String> start() {

        final String dispatcherPath = "akka.actor.default-blocking-io-dispatcher";
        Props greeterProps = DispatcherSelector.fromConfig(dispatcherPath);

        ActorRef<String> supervisingActor = getContext().spawn(SupervisingActor.create(), "supervising-actor",greeterProps);
        supervisingActor.tell("failChild");

        return Behaviors.same();
    }
}
