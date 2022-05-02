package com.classic;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import static akka.pattern.Patterns.gracefulStop;
import akka.pattern.AskTimeoutException;
import java.util.concurrent.CompletionStage;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class E9StopActorExample {
    public static void main(String[] args) throws InterruptedException {
        ActorSystem system = ActorSystem.create("testSystem");
        ActorRef fatherActor = system.actorOf(MyStoppingActor.props(), "fatherActor");

        fatherActor.tell("interrupt-child", ActorRef.noSender());
        Thread.sleep(1000);


        fatherActor.tell("done", ActorRef.noSender());
        Thread.sleep(1000);
        fatherActor.tell("test is stop or not", ActorRef.noSender());

        ActorRef poisonPillActor = system.actorOf(MyStoppingActor.props(), "poisonPillActor");
        poisonPillActor.tell(akka.actor.PoisonPill.getInstance(), ActorRef.noSender());
        Thread.sleep(1000);
        poisonPillActor.tell("test is stop or not", ActorRef.noSender());

        ActorRef killingActor = system.actorOf(MyStoppingActor.props(), "killingActor");
        killingActor.tell(akka.actor.Kill.getInstance(), ActorRef.noSender());
        Thread.sleep(1000);
        killingActor.tell("test is stop or not", ActorRef.noSender());


        ActorRef gracefulStopActor = system.actorOf(MyStoppingActor.props(), "gracefulStopActor");
        try {
            CompletionStage<Boolean> stopped =
                    gracefulStop(gracefulStopActor, Duration.ofSeconds(5), "done");
            Thread.sleep(10000);
            stopped.toCompletableFuture().get(3, TimeUnit.SECONDS);
            // the actor has been stopped
        } catch (Exception e) {
            System.err.println("exception");
            // the actor wasn't stopped within 5 seconds
        }
        gracefulStopActor.tell("test is stop or not", ActorRef.noSender());

        System.out.println("end");
    }
}


 class MyStoppingActor extends AbstractActor {

     private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef child = getContext().actorOf(MyStoppingActor.props(), "childActor");

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals("interrupt-child", m -> {
                    log.info("Received String message: {}", m);
                    getContext().stop(child);
                })
                .matchEquals("done", m -> {
                    log.info("Received String message: {}", m);
                    getContext().stop(getSelf());
                })
                .matchAny(o -> log.info("received unknown message: " + o))
                .build();
    }

     @Override
     public void postStop() throws Exception {
        log.info("this is : "+getContext());
        super.postStop();

     }

     static Props props(){
         return Props.create(MyStoppingActor.class);
     }
}
