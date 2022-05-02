package com.classic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class E4SendMessageExample {

    private static ActorSystem system = ActorSystem.create("testSystem");
    static ActorRef firstActor = system.actorOf(TempActor.props(), "firstActor");
    static ActorRef secondActor = system.actorOf(TempActor.props(), "secondActor");
    static ActorRef thirdActor = system.actorOf(TempActor.props(), "thirdActor");


    public static void main(String[] args) throws InterruptedException {

        firstActor.tell("send", ActorRef.noSender());
        Thread.sleep(1000);
        firstActor.tell("forward1", secondActor);
        Thread.sleep(1000);
        firstActor.tell("Hi I am in the main", secondActor);
        Thread.sleep(1000);
        firstActor.tell("Hi I am in the main and I am no body", ActorRef.noSender());

    }
}

class TempActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(
                        "send",
                        s -> {

                            E4SendMessageExample.secondActor.tell("Hi I am in the actor", getSelf());
                        })
                .matchEquals(
                        "forward1",
                        s -> {

                            E4SendMessageExample.thirdActor.forward(s, getContext());
                        })
                .match(String.class,
                        s -> {
                            log.info("sender: {}", getSender());
                            log.info("Received String message: {}", s);
                        })
                .matchAny(o -> log.info("received unknown message: " + o))
                .build();
    }

    static Props props() {
        return Props.create(TempActor.class);
    }
}


