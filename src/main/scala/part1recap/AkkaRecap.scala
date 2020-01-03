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

}
