package com.example.supervising;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.SupervisorStrategy;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

class SupervisingActor extends AbstractBehavior<String> {

    private final ActorRef<String> childByRestartStrategy;
    private final ActorRef<String> childByResumeStrategy;
    private final ActorRef<String> childByStopStrategy;

    private SupervisingActor(ActorContext<String> context) {
        super(context);
        childByRestartStrategy =
                context.spawn(
                        Behaviors.supervise(SupervisedActor.create()).onFailure(SupervisorStrategy.restart()),
                        "supervised-actor-by-restart");
        childByResumeStrategy =
                context.spawn(
                        Behaviors.supervise(SupervisedActor.create()).onFailure(SupervisorStrategy.resume()),
                        "supervised-actor-by-resume");
        childByStopStrategy =
                context.spawn(
                        Behaviors.supervise(SupervisedActor.create()).onFailure(SupervisorStrategy.stop()),
                        "supervised-actor-by-stop");

        System.out.println("parent: " + Thread.currentThread());
    }

    static Behavior<String> create() {
        return Behaviors.setup(SupervisingActor::new);
    }

    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder().onMessageEquals("failChild", this::onFailChild).build();
    }

    private Behavior<String> onFailChild() {
        childByRestartStrategy.tell("fail");
        childByResumeStrategy.tell("fail");
        childByStopStrategy.tell("fail");
        return this;
    }
}