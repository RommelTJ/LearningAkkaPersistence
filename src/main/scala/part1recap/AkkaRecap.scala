package part1recap

import akka.actor.Actor

object AkkaRecap extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message => println(s"I received: $message")
    }
  }

}
