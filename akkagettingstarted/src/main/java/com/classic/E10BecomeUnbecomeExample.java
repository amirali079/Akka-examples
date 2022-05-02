package com.classic;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class E10BecomeUnbecomeExample {

    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("testSystem");
        ActorRef hotSwapActor = system.actorOf(HotSwapActor.props(), "hotSwapActor");
        ActorRef sender = system.actorOf(HotSwapActor.props(), "sender");

//        hotSwapActor.tell("foo", sender);
//        hotSwapActor.tell("foo", sender);
//        hotSwapActor.tell("bar", sender);
//        hotSwapActor.tell("bar", sender);
//        hotSwapActor.tell("foo", sender);
//
//        Thread.sleep(1000);
//        hotSwapActor.tell("Hello", sender);
//        hotSwapActor.tell("bar", sender);


        ActorRef swapper = system.actorOf(Swapper.props(), "swapper");
        swapper.tell(Swapper.Swap, ActorRef.noSender()); // logs Hi
        swapper.tell(Swapper.Swap, ActorRef.noSender()); // logs Ho
        swapper.tell(Swapper.Swap, ActorRef.noSender()); // logs Hi
        swapper.tell(Swapper.Swap, ActorRef.noSender()); // logs Ho
        swapper.tell(Swapper.Swap, ActorRef.noSender()); // logs Hi

        swapper.tell(Swapper.in, ActorRef.noSender()); // logs in
        swapper.tell(Swapper.Swap, ActorRef.noSender()); // logs in
        swapper.tell(Swapper.Swap, ActorRef.noSender()); // logs Ho
        swapper.tell(Swapper.Swap, ActorRef.noSender()); // logs Hi
        system.terminate();

    }
}

class HotSwapActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private AbstractActor.Receive angry;
    private AbstractActor.Receive happy;

    public HotSwapActor() {
        angry =
                receiveBuilder()
                        .matchEquals(
                                "foo",
                                s -> {
                                    getSender().tell("I am already angry?", getSelf());
                                })
                        .matchEquals(
                                "bar",
                                s -> {
                                    getContext().become(happy);
                                })
                        .build();

        happy =
                receiveBuilder()
                        .matchEquals(
                                "bar",
                                s -> {
                                    getSender().tell("I am already happy :-)", getSelf());
                                })
                        .matchEquals(
                                "foo",
                                s -> {
                                    //getContext().become(angry);
                                    getContext().unbecome();
                                })
                        .build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("foo", s -> {
                    log.info(getSelf() + ": " + s);
                    getContext().become(angry);
                })
                .matchEquals("bar", s -> {
                    log.info(getSelf() + ": " + s);
                    getContext().become(happy);
                })
                .match(String.class, s ->
                {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message: " + o))
                .build();
    }

    static Props props() {
        return Props.create(HotSwapActor.class);
    }
}

class Swapper extends AbstractLoggingActor {

    public static Object Swap = "";
    public static Object in = "in";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(
                        Swap,
                        s -> {
                            log().info("Hi");
                            getContext()
                                    .become(
                                            receiveBuilder()
                                                    .matchEquals(
                                                            Swap,
                                                            x -> {
                                                                log().info("Ho");
                                                                getContext()
                                                                        .unbecome(); // resets the latest 'become' (just for fun)
                                                            })
                                                    .matchEquals(
                                                            in,
                                                            x -> {
                                                                log().info("Hi in");
                                                                getContext()
                                                                        .become(
                                                                                receiveBuilder()
                                                                                        .matchEquals(
                                                                                                Swap,
                                                                                                y -> {
                                                                                                    log().info("Ho in");
                                                                                                    getContext()
                                                                                                            .unbecome(); // resets the latest 'become' (just for fun)
                                                                                                })
                                                                                        .build(), false);
                                                            })
                                                    .build(),
                                            false); // push on top instead of replace
                        })
                .build();
    }

    static Props props() {
        return Props.create(Swapper.class);
    }
}

