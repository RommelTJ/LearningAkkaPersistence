package part2eventsourcing

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object RecoveryDemo extends App {

  class RecoveryActor extends PersistentActor with ActorLogging {
    
    override def persistenceId: String = "recover-actor"

    override def receiveCommand: Receive = ???

    override def receiveRecover: Receive = ???

  }

}
