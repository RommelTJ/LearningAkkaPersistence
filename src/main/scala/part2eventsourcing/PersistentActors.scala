package part2eventsourcing

import java.util.Date

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object PersistentActors extends App {

  /**
   * Scenario: We have a business and an accountant which keeps track of our invoices.
   */

  // Commands
  case class Invoice(recipient: String, date: Date, amount: Int)

  // Events
  case class InvoiceRecorded(id: Int, recipient: String, date: Date, amount: Int)

  // Persistent Actor
  class Accountant extends PersistentActor with ActorLogging {

    var latestInvoiceId = 0
    var totalAmount = 0

    // How the events persistent by this Actor will be identified.
    override def persistenceId: String = "simple-accountant" // best practice: Make it unique

    // The normal receive method.
    override def receiveCommand: Receive = {
      case Invoice(recipient, date, amount) =>
        // When you receive a command:
        // 1) You create an EVENT to persist into the store
        // 2) You persist the event, then pass in a callback that will get triggered once the event is written
        // 3) You update the Actor's state when the event has persisted.
        log.info(s"Received invoice for amount: $amount")
        val event = InvoiceRecorded(latestInvoiceId, recipient, date, amount)
        persist(event){ e =>
          latestInvoiceId += 1
          totalAmount += amount
          log.info(s"Persisted $e as invoice #${e.id}, for total amount: $totalAmount")
        }
    }

    // The handler that is called on recovery.
    override def receiveRecover: Receive = ???

  }

  val system = ActorSystem("PersistentActors")
  val accountant = system.actorOf(Props[Accountant], "simpleAccountant")

  for (i <- 1 to 10) {
    accountant ! Invoice("The Sofa Company", new Date, i * 1000)
  }

}
