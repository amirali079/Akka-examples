package com.classic;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class E6WatchActorExample {
    static ActorSystem system = ActorSystem.create("testSystem");
    static final ActorRef targetActor = system.actorOf(Props.empty(), "target");
    static ActorRef lastSender = system.deadLetters();

    public static void main(String[] args) {



        ActorRef watchActor = system.actorOf(WatchActor.props(),"watchActor");
        ActorRef senderActor = system.actorOf(SenderActor.props(),"senderActor");

        senderActor.tell("kill the target",ActorRef.noSender());


    }

}

class SenderActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef killerActor = getContext().actorOf(KillerActor.props(), "killerActor");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(
                        "finished",
                        s -> {
                            log.info("Received String message: {}", s);
                        })
                .match(
                        String.class,
                        s -> {
                            log.info("Received String message: {}", s);
                            killerActor.tell("kill",getSelf());
                        })
                .build();
    }

     static Props props(){
        return Props.create(SenderActor.class);
    }
}


class KillerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                 .matchEquals(
                "kill",
                s -> {
                    E6WatchActorExample.lastSender = getSender();
                    getContext().stop(E6WatchActorExample.targetActor);

                })
                .build()
                ;
    }

     static Props props(){
        return Props.create(KillerActor.class);
    }
}

class WatchActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

   public WatchActor() {
       getContext().watch(E6WatchActorExample.targetActor); // <-- this is the only call needed for registration
   }

   @Override
   public Receive createReceive() {
       return receiveBuilder()
               .match(
                       Terminated.class,
                       t -> t.actor().equals(E6WatchActorExample.targetActor),
                       t -> {
                           E6WatchActorExample.lastSender.tell("finished", getSelf());
                       })
               .build();
   }

     static Props props(){
        return Props.create(WatchActor.class);
    }
}
