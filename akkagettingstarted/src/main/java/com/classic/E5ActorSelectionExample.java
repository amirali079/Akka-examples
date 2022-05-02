package com.classic;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class E5ActorSelectionExample {

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("testSystem");

        ActorRef actorCanNotSelect = system.actorOf(SelectorActor.props(), "actorCanNotSelect");
        Thread.sleep(1000);

        ActorRef another = system.actorOf(SelectedActor.props(), "selected");
        System.out.println("create actor in path: " + another.path());

        ActorRef actorCanSelect = system.actorOf(SelectorActor.props(), "actorCanSelect");


    }
}

class SelectorActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    public SelectorActor() {
        ActorSelection selection = getContext().actorSelection("/user/selected");

        selection.tell(new Identify(1), getSelf());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        ActorIdentity.class,
                        id -> id.getActorRef().isPresent(),
                        id -> {
                            ActorRef ref = id.getActorRef().get();
                            ref.tell("i know you", getSelf());

                            log.info("in receive");
                        })
                .match(
                        ActorIdentity.class,
                        id -> !id.getActorRef().isPresent(),
                        id -> {
                            getContext().stop(getSelf());

                            log.info("in other receive");

                        })
                .match(
                        String.class,
                        s -> {
                            log.info("sender: {}", getSender());
                            log.info("Received String message: {}", s);
                        })
                .build();
    }


    static Props props() {
        return Props.create(SelectorActor.class);
    }
}

class SelectedActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        String.class,
                        s -> {
                            log.info("sender: {}", getSender());
                            log.info("Received String message: {}", s);
                        })
                .build();
    }

    static Props props() {
        return Props.create(SelectedActor.class);
    }
}




