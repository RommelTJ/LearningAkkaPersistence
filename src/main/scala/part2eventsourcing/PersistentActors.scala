package part2eventsourcing

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object PersistentActors extends App {

  /**
   * Scenario: We have a business and an accountant which keeps track of our invoices.
   */
  class Accountant extends PersistentActor with ActorLogging {

    override def persistenceId: String = ???

    override def receiveCommand: Receive = ???

    override def receiveRecover: Receive = ???

  }

}
