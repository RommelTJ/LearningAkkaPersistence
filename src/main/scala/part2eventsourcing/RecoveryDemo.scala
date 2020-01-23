package part2eventsourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

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
   */

}
