package com.example.print;

import akka.actor.typed.ActorSystem;

public class ActorHierarchyExperiments {
    public static void main(String[] args) {
        ActorSystem<String> testSystem = ActorSystem.create(Main.create(), "testSystem");
        testSystem.tell("startFirstExample");
        try {
            Thread.sleep(2000);
            System.out.println(testSystem.printTree());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




    }

}



