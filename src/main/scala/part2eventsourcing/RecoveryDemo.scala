package part2eventsourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, Recovery, SnapshotSelectionCriteria}

object RecoveryDemo extends App {

  case class Command(contents: String)
  case class Event(contents: String)

  class RecoveryActor extends PersistentActor with ActorLogging {

    override def persistenceId: String = "recover-actor"

    override def receiveCommand: Receive = {
      case Command(contents) =>
        persist(Event(contents)) { event =>
          log.info(s"Successfully persisted $event")
        }
    }

    override def receiveRecover: Receive = {
      case Event(contents) =>
        if (contents.contains("314")) throw new RuntimeException("kaboom")
        log.info(s"Recovered: $contents")
    }

    override protected def onRecoveryFailure(cause: Throwable, event: Option[Any]): Unit = {
      log.error(s"I failed at recovery")
      super.onRecoveryFailure(cause, event)
    }

    // override def recovery: Recovery = Recovery(toSequenceNr = 100)
    // override def recovery: Recovery = Recovery(fromSnapshot = SnapshotSelectionCriteria.Latest)
    // override def recovery: Recovery = Recovery.none

  }

  val system = ActorSystem("RecoveryDemo")
  val recoveryActor = system.actorOf(Props[RecoveryActor], "recoveryActor")

  /**
   * Stashing commands
   */
//  for (i <- 1 to 1000) {
//    recoveryActor ! Command(s"command: $i")
//  }
  // ALL COMMANDS SENT DURING RECOVERY ARE STASHED

  /**
   * 2 - Failure during recovery
   *   - onRecoveryFailure + the actor is stopped (if there is a failure, the Actor cannot be trusted anymore)
   */

  /**
   * 3 - Customizing recovery
   *   - WARNING: Please DO NOT persist more events after a customized _incomplete_ recovery
   *              You might corrupt messages in the meantime.
   */

}
