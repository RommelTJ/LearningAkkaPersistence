package part2eventsourcing

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.persistence.PersistentActor

object PersistAsyncDemo extends App {

  case class Command(contents: String)
  case class Event(contents: String)

  class CriticalStreamProcessor(eventAggregator: ActorRef) extends PersistentActor with ActorLogging {

    override def persistenceId: String = "critical-stream-processor"

    override def receiveCommand: Receive = {
      case Command(contents) =>
        eventAggregator ! s"Processing $contents"
        persist(Event(contents)) { e =>
          eventAggregator ! e
        }

        // some actual computation
        val processedContents = contents + "_processed"
        persist(Event(processedContents)) { e =>
          eventAggregator ! e
        }
    }

    override def receiveRecover: Receive = {
      case message => log.info(s"Recovered: $message")
    }

  }
  object CriticalStreamProcessor {
    def props(eventAggregator: ActorRef) = Props(new CriticalStreamProcessor(eventAggregator))
  }

  class EventAggregator extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(s"Aggregating $message")
    }
  }

}
