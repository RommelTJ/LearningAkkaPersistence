package part1recap

import akka.actor.{Actor, ActorSystem, Props, Stash}

object AkkaRecap extends App {

  class SimpleActor extends Actor with Stash {
    override def receive: Receive = {
      case "createChild" =>
        val childActor = context.actorOf(Props[SimpleActor], "myChild")
        childActor ! "hello"
      case "stashThis" => stash()
      case "change handler now" =>
        unstashAll()
        context.become(anotherHandler)
      case "change" => context.become(anotherHandler)
      case message => println(s"I received: $message")
    }

    def anotherHandler: Receive = {
      case message => println(s"In another receive handler with message: $message")
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

  // changing actor behavior + stashing
  // Actors can spawn other actors

}
