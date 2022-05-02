package com.classic;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class E2SenderReceiverExample {

    private static ActorSystem system = ActorSystem.create("testSystem");
    public static void main(String[] args) {


        ActorRef actor = system.actorOf(SomeOtherActor.props(),"testActor");

        actor.tell("Chakerim",ActorRef.noSender());



    }
}

class DemoActor extends AbstractActor {
   /**
    * Create Props for an actor of this type.
    *
    * @param magicNumber The magic number to be passed to this actor’s constructor.
    * @return a Props for creating this actor, which can then be further configured (e.g. calling
    *     `.withDispatcher()` on it)
    */
   static Props props(Integer magicNumber) {
       // You need to specify the actual type of the returned actor
       // since Java 8 lambdas have some runtime type information erased
       return Props.create(DemoActor.class, () -> new DemoActor(magicNumber));
   }
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
   private final Integer magicNumber;

   private DemoActor(Integer magicNumber) {
       this.magicNumber = magicNumber;
   }

   @Override
   public Receive createReceive() {
       return receiveBuilder()
               .match(
                       Integer.class,
                       i -> {
                           log.info("my magic number: {}",magicNumber);
                           log.info("Received String message: {}", i);
                           getSender().tell(i + magicNumber, getSelf());
                       })
               .matchAny(o -> log.info("received unknown message"))
               .build();
   }
}

 class SomeOtherActor extends AbstractActor {
    // Props(new DemoActor(42)) would not be safe
    private ActorRef demoActor = getContext().actorOf(DemoActor.props(42), "demo");
     private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

     @Override
     public Receive createReceive() {
         return receiveBuilder()
                 .match(
                         String.class,
                         s -> {
                            log.info("Received String message: {}", s);
                             demoActor.tell(32,getSelf());
                         })
                 .match(
                         Integer.class,
                         i -> {
                             log.info("Received String message: {}", i);
                         })
                 .build();
     }
     static Props props(){
         return Props.create(SomeOtherActor.class);
     }
}

