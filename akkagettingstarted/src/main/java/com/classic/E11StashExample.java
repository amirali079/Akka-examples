package com.classic;

import akka.actor.AbstractActorWithStash;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class E11StashExample {

    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("testSystem");
        ActorRef actorWithProtocol = system.actorOf(ActorWithProtocol.props(), "actorWithProtocol");

        actorWithProtocol.tell("open",ActorRef.noSender());
        actorWithProtocol.tell("write",ActorRef.noSender());
        actorWithProtocol.tell("test1",ActorRef.noSender());
        actorWithProtocol.tell("test2",ActorRef.noSender());
        actorWithProtocol.tell("write",ActorRef.noSender());
        actorWithProtocol.tell("close",ActorRef.noSender());
        actorWithProtocol.tell("write",ActorRef.noSender());

    }

}

 class ActorWithProtocol extends AbstractActorWithStash {

     private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(
                        "open",
                        s -> {
                            getContext()
                                    .become(
                                            receiveBuilder()
                                                    .matchEquals(
                                                            "write",
                                                            ws -> {
                                                                log.info("Received String message: {}", ws);
                                                                /* do writing */
                                                            })
                                                    .matchEquals(
                                                            "close",
                                                            cs -> {
                                                                unstashAll();
                                                                getContext().unbecome();
                                                            })

                                                    .matchAny(msg -> stash())

                                                    .build(),
                                            false);
                        })
                .matchAny(msg -> log.info("Received String message: {}", msg))
                .build();
    }

     static Props props() {
         return Props.create(ActorWithProtocol.class);
     }
}
