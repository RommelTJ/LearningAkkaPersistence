package part2eventsourcing

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistAsyncDemo extends App {

  case class Command(contents: String)
  case class Event(contents: String)

  class CriticalStreamProcessor(eventAggregator: ActorRef) extends PersistentActor with ActorLogging {

    override def persistenceId: String = "critical-stream-processor"

    override def receiveCommand: Receive = {
      case Command(contents) =>
        eventAggregator ! s"Processing $contents"
        persistAsync(Event(contents)) /*      TIME GAP      */ { e =>
          eventAggregator ! e
        }

        // some actual computation
        val processedContents = contents + "_processed"
        persistAsync(Event(processedContents)) /*      TIME GAP      */ { e =>
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
      case message => log.info(s"$message")
    }
  }

  val actorSystem = ActorSystem("PersistAsyncDemo")
  val eventAggregator = actorSystem.actorOf(Props[EventAggregator], "eventAggregator")
  val streamProcessor = actorSystem.actorOf(CriticalStreamProcessor.props(eventAggregator), "streamProcessor")

  streamProcessor ! Command("command1")
  streamProcessor ! Command("command2")
  // When using persistAsync, you can also receive normal commands in between time gaps. Relaxes the persistence
  // guarantees. However, both persist and persistAsync are asynchronous.

  /**
   * When should you use persist vs persistAsync?
   * - persistAsync has a performance benefit over persist. Useful in high-throughput environments.
   * - persistAsync is bad when you absolutely need the ordering of the events (e.g. your state depends on it).
   * - Thus, the main reason to choose one over the other is ordering guarantees.
   */

}
