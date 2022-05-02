package com.example.supervising;

import akka.actor.typed.ActorSystem;

public class Supervising {
    public static void main(String[] args) {
        ActorSystem<String> testSystem = ActorSystem.create(Main.create(), "testSystem");

        testSystem.tell("startThirdExample");
        try {
            Thread.sleep(2000);
            System.out.println(testSystem.printTree());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        testSystem.terminate();

    }


}
