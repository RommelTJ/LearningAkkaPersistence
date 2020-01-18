package part2eventsourcing

import java.util.Date

import akka.actor.{ActorLogging, ActorSystem, PoisonPill, Props}
import akka.persistence.PersistentActor

object PersistentActors extends App {

  /**
   * Scenario: We have a business and an accountant which keeps track of our invoices.
   */

  // Commands
  case class Invoice(recipient: String, date: Date, amount: Int)
  case class InvoiceBulk(invoices: List[Invoice])

  // Special Commands
  case object Shutdown

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
      case InvoiceBulk(invoices) =>
        // 1) Create events (plural)
        val invoiceIds = latestInvoiceId to (latestInvoiceId + invoices.size)
        val events = invoices.zip(invoiceIds).map { pair =>
          val id = pair._2
          val invoice = pair._1
          InvoiceRecorded(id, invoice.recipient, invoice.date, invoice.amount)
        }
        // 2) Persist all the events
        persistAll(events) { e =>
          // 3) Update the actor state when each event is persisted
          latestInvoiceId += 1
          totalAmount += e.amount
          log.info(s"Persisted SINGLE $e as invoice #${e.id}, for total amount: $totalAmount")
        }
      case Shutdown => context.stop(self)
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

    // Called if the JOURNAL fails to persist the event. The actor is RESUMED.
    override protected def onPersistRejected(cause: Throwable, event: Any, seqNr: Long): Unit = {
      log.error(s"Persist rejected for $event because of $cause")
      super.onPersistRejected(cause, event, seqNr)
    }

  }

  val system = ActorSystem("PersistentActors")
  val accountant = system.actorOf(Props[Accountant], "simpleAccountant")

  for (i <- 1 to 10) {
    accountant ! Invoice("The Sofa Company", new Date, i * 1000)
  }

  /*
     Persistence failures
   */
  /**
   * Persisting multiple events
   *
   * persistAll
   */
  val newInvoices = for (i <- 1 to 5) yield Invoice("The awesome chairs", new Date, i * 2000)
  // accountant ! InvoiceBulk(newInvoices.toList)

  /*
     NEVER EVER CALL PERSIST OR PERSISTALL FROM FUTURES. YOU RISK BREAKING ACTOR ENCAPSULATION.
     The actor thread is free to process messages while you're persisting. If the normal actor thread also calls persist,
     you suddenly have two threads persisting events simultaneously. Since event order is non-deterministic, you risk
     corrupting the actor state.
   */

  /**
   * Shutdown of persistent actors
   *
   * Best practice: Define your own "shutdown" messages.
   */
  // accountant ! PoisonPill
  accountant ! Shutdown

}
