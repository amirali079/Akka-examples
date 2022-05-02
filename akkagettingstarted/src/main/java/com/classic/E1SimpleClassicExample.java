package com.classic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class E1SimpleClassicExample {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("testSystem");

        ActorRef actor = system.actorOf(MyActor.props(),"testActor");

        actor.tell("Chakerim",ActorRef.noSender());
        actor.tell(123,ActorRef.noSender());
        actor.tell(true,ActorRef.noSender());
    }
}

class MyActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        String.class,
                        s -> {
                            log.info("Received String message: {}", s);
                        })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
    static Props props(){
        return Props.create(MyActor.class);
    }
}
