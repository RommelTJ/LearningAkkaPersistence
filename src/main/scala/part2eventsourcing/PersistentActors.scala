package part2eventsourcing

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object PersistentActors extends App {

  /**
   * Scenario: We have a business and an accountant which keeps track of our invoices.
   */
  class Accountant extends PersistentActor with ActorLogging {

    // How the events persistent by this Actor will be identified.
    override def persistenceId: String = "simple-accountant" // best practice: Make it unique

    override def receiveCommand: Receive = ???

    override def receiveRecover: Receive = ???

  }

}
