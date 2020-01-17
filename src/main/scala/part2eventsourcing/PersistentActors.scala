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
        // val event = InvoiceRecorded(latestInvoiceId, recipient, date, amount)
        persist(InvoiceRecorded(latestInvoiceId, recipient, date, amount))
        /* time gap: all other messages sent to this actor are STASHED */
        { e =>
          // Update state
          // SAFE to access mutable state because you don't have race conditions
          // Akka persistence guarantees that no other threads are accessing the actor during the callback
          latestInvoiceId += 1
          totalAmount += amount
          // We can even correctly identify the sender of the Command
          sender() ! "PersistenceACK"
          log.info(s"Persisted $e as invoice #${e.id}, for total amount: $totalAmount")
        }
      // You don't need to persist events. You can act like a normal actor
      case "print" =>
        log.info(s"Latest invoice id: $latestInvoiceId, total amount: $totalAmount")
    }

    // The handler that is called on recovery.
    override def receiveRecover: Receive = {
      // Best practice: Follow the logic in the persist steps of receiveCommand
      case InvoiceRecorded(id, _, _, amount) =>
        latestInvoiceId = id
        totalAmount += amount
        log.info(s"Recovered invoice #$id for amount: $amount, total amount: $totalAmount")
    }

    // This method is called if persisting failed.
    // The actor will be STOPPED (regardless of the supervision strategy).
    // Best practice: Start the actor again after a while (use Backoff supervisor)
    override protected def onPersistFailure(cause: Throwable, event: Any, seqNr: Long): Unit = {
      log.error(s"Fail to persist $event because of $cause")
      super.onPersistFailure(cause, event, seqNr)
    }
  }

  val system = ActorSystem("PersistentActors")
  val accountant = system.actorOf(Props[Accountant], "simpleAccountant")

//  for (i <- 1 to 10) {
//    accountant ! Invoice("The Sofa Company", new Date, i * 1000)
//  }

  /*
     Persistence failures
   */

}
