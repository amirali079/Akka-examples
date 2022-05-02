package com.example.akkaQuickstart;

import akka.actor.typed.ActorSystem;

import java.io.IOException;
public class AkkaQuickstart {
  public static void main(String[] args) {
    //#actor-system
    final ActorSystem<GreeterMain.SayHello> greeterMain = ActorSystem.create(GreeterMain.create(),
            "helloakka");
    //#actor-system

    //#main-send-messages
    System.out.println("in akka quick start " + greeterMain.printTree());

    greeterMain.tell(new GreeterMain.SayHello("Charles"));
    //#main-send-messages
    try {
      Thread.sleep(2000);
      System.out.println(greeterMain.printTree());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }


    try {
      System.out.println(">>> Press ENTER to exit <<<");
      System.in.read();
    } catch (IOException ignored) {
    } finally {
      greeterMain.terminate();
    }
  }
}
