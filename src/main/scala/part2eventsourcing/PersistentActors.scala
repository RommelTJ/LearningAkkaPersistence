package part2eventsourcing

import java.util.Date

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistentActors extends App {

  /**
   * Scenario: We have a business and an accountant which keeps track of our invoices.
   */

  case class Invoice(recipient: String, date: Date, amount: Int)

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

  for (i <- 1 to 10) {
    accountant ! Invoice("The Sofa Company", new Date, i * 1000)
  }

}
