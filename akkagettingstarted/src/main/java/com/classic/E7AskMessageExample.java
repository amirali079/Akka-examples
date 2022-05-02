package com.classic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class E7AskMessageExample {
    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("testSystem");
        ActorRef actorA = system.actorOf(AlphabetActor.props(), "actorA");
        ActorRef actorB = system.actorOf(AlphabetActor.props(), "actorB");
        ActorRef actorC = system.actorOf(AlphabetActor.props(), "actorC");


        final Duration t = Duration.ofSeconds(5);

// using 1000ms timeout
        CompletableFuture<Object> future1 =
                ask(actorA, "request", Duration.ofMillis(1000)).toCompletableFuture();

// using timeout from above
        CompletableFuture<Object> future2 = ask(actorB, "another request", t).toCompletableFuture();

        CompletableFuture<String> transformed =
                CompletableFuture.allOf(future1, future2)
                        .thenApply(
                                v -> {

                                    String x = (String) future1.join();
                                    String s = (String) future2.join();
                                    return x + "/" + s;

                                });

        pipe(transformed, system.dispatcher()).to(actorC);

    }
}

class AlphabetActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(
                        "request",
                        s -> {
                            log.info("sender: {}", getSender());
                            getSender().tell("response", getSelf());
                        })
                .matchEquals(
                        "another request",
                        s -> {
                            log.info("sender: {}", getSender());
                              getSender().tell("another response", getSelf());
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
        return Props.create(AlphabetActor.class);
    }
}


