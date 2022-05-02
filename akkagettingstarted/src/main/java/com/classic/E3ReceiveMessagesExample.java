package com.classic;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class E3ReceiveMessagesExample {


    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("testSystem");

        ActorRef wellStructuredActor = system.actorOf(WellStructuredActor.props(),"wellStructuredActor");

        wellStructuredActor.tell(new WellStructuredActor.Msg1(),ActorRef.noSender());
        wellStructuredActor.tell(new WellStructuredActor.Msg2(),ActorRef.noSender());
        wellStructuredActor.tell(new WellStructuredActor.Msg3(),ActorRef.noSender());

        ActorRef optimizedActor = system.actorOf(OptimizedActor.props(),"optimizedActor");

        optimizedActor.tell(new OptimizedActor.Msg1(),ActorRef.noSender());
        optimizedActor.tell(new OptimizedActor.Msg2(),ActorRef.noSender());
        optimizedActor.tell(new OptimizedActor.Msg3(),ActorRef.noSender());
    }


}


 class WellStructuredActor extends AbstractActor {

     private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

     public static class Msg1 {}

     public static class Msg2 {}

     public static class Msg3 {}

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Msg1.class, this::receiveMsg1)
                .match(Msg2.class, this::receiveMsg2)
                .match(Msg3.class, this::receiveMsg3)
                .build();
    }

    private void receiveMsg1(Msg1 msg) {
        log.info("massage 1 received");

    }

    private void receiveMsg2(Msg2 msg) {
        log.info("massage 2 received");
    }

    private void receiveMsg3(Msg3 msg) {
        log.info("massage 3 received");
    }


     static Props props(){
         return Props.create(WellStructuredActor.class);
     }
}

 class OptimizedActor extends UntypedAbstractActor {

     private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public static class Msg1 {}

    public static class Msg2 {}

    public static class Msg3 {}

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof Msg1) receiveMsg1((Msg1) msg);
        else if (msg instanceof Msg2) receiveMsg2((Msg2) msg);
        else if (msg instanceof Msg3) receiveMsg3((Msg3) msg);
        else unhandled(msg);
    }

     private void receiveMsg1(Msg1 msg) {
         log.info("massage 1 received");

     }

     private void receiveMsg2(Msg2 msg) {
         log.info("massage 2 received");
     }

     private void receiveMsg3(Msg3 msg) {
         log.info("massage 3 received");
     }

     static Props props(){
         return Props.create(OptimizedActor.class);
     }
}