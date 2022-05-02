package com.classic;
import java.time.Duration;
import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;


public class E8TimerSchedulerExample {

    public static void main(String[] args) {

        ActorSystem system = ActorSystem.create("testSystem");

        ActorRef timerActor = system.actorOf(TimerActor.props(),"timerActor");
    }
}

 class TimerActor extends AbstractActorWithTimers {

     private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

     private long time = System.currentTimeMillis();

    private static Object TICK_KEY = "TickKey";

    private static final class FirstTick {}

    private static final class Tick {}

    public TimerActor() {
        getTimers().startSingleTimer(TICK_KEY, new FirstTick(), Duration.ofMillis(500));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(
                        FirstTick.class,
                        message -> {
                            // do something useful here
                            log.info("FirstTick");
                            getTimers().startTimerWithFixedDelay(TICK_KEY, new Tick(), Duration.ofSeconds(2));
                        })
                .match(
                        Tick.class,
                        message -> {

                            log.info("Tick: "+(System.currentTimeMillis()- time)/1000);
                             time = System.currentTimeMillis();
                            // do something useful here
                        })
                .build();
    }

     static Props props() {
         return Props.create(TimerActor.class);
     }
}