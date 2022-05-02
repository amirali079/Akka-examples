package com.example.start_stop;

import akka.actor.typed.ActorSystem;

public class StartStop {
    public static void main(String[] args) {
        ActorSystem<String> testSystem = ActorSystem.create(Main.create(), "testSystem");
        testSystem.tell("startSecondExample");
        try {
            Thread.sleep(2000);
            System.out.println(testSystem.printTree());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
