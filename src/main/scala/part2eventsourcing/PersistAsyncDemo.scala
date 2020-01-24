package part2eventsourcing

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object PersistAsyncDemo extends App {

  case class Command(contents: String)
  case class Event(contents: String)

  class CriticalStreamProcessor extends PersistentActor with ActorLogging {

    override def persistenceId: String = "critical-stream-processor"

    override def receiveCommand: Receive = ???

    override def receiveRecover: Receive = {
      case message => log.info(s"Recovered: $message")
    }

  }

}