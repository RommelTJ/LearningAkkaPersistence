package part1recap

import akka.actor.{Actor, ActorSystem, Props}

object AkkaRecap extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => println(s"I received: $message")
    }
  }

  // Actor encapsulation
  val system = ActorSystem("AkkaRecap")
  // new SimpleActor // throws Exception! You cannot create an instance using the constructor.
  val actor = system.actorOf(Props[SimpleActor], "simpleActor")

  // Sending messages
  actor ! "hello"

  // Messages are sent asynchronously
  // Many actors (in the millions) can share a few dozen threads
  // Each message is processed/handled ATOMICALLY
  // No need for locks

}
