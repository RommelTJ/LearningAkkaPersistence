package part2eventsourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistentActors extends App {

  /**
   * Scenario: We have a business and an accountant which keeps track of our invoices.
   */
  class Accountant extends PersistentActor with ActorLogging {

    // How the events persistent by this Actor will be identified.
    override def persistenceId: String = "simple-accountant" // best practice: Make it unique

    // The normal receive method.
    override def receiveCommand: Receive = ???

    // The handler that is called on recovery.
    override def receiveRecover: Receive = ???

  }

  val system = ActorSystem("PersistentActors")
  val accountant = system.actorOf(Props[Accountant], "simpleAccountant")

}
