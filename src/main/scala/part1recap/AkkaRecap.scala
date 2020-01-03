package part1recap

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, PoisonPill, Props, Stash, SupervisorStrategy}

object AkkaRecap extends App {

  class SimpleActor extends Actor with Stash with ActorLogging {
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

    override def preStart(): Unit = {
      log.info(s"I'm starting")
    }

    override def supervisorStrategy: SupervisorStrategy = {
      OneForOneStrategy() {
        case _: RuntimeException => Restart
        case _ => Stop
      }
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
  // Guardians: /system, /user, / = root guardian

  // Actors have a defined lifecycle:
  // They can be started, stopped, suspended, resumed, restarted.

  // Stopping Actors
  // context.stop
  actor ! PoisonPill

  // Logging

  // Supervision
  // How parent actors are going to respond to child actor failures

  // Configure Akka infrastructure
  // dispatchers, routers, mailboxes

  // Schedulers
  import scala.concurrent.duration._
  import system.dispatcher
  system.scheduler.scheduleOnce(2 seconds) {
    actor ! "delayed happy birthday"
  }

}
