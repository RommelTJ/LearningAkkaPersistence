package part2eventsourcing

import java.util.Date

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.persistence.PersistentActor

object MultiplePersists extends App {

  /**
   * Diligent Account
   * With every invoice, this actor will persist TWO events.
   * - a tax record for the fiscal authority
   * - an invoice record for personal logs or some auditing authority
   */

  // Command
  case class Invoice(recipient: String, date: Date, amount: Int)

  // Events
  case class TaxRecord(taxId: String, recordId: Int, date: Date, totalAmount: Int)
  case class InvoiceRecord(invoiceRecordId: Int, recipient: String, date: Date, amount: Int)

  class DiligentAccountant(taxId: String, taxAuthority: ActorRef) extends PersistentActor with ActorLogging {

    var latestTaxRecordId = 0
    var latestInvoiceRecordId = 0

    override def persistenceId: String = "diligent-accountant"

    override def receiveCommand: Receive = {
      case Invoice(recipient, date, amount) =>
        persist(TaxRecord(taxId, latestTaxRecordId, date, amount / 3)) { record =>
          taxAuthority ! record
          latestTaxRecordId += 1
        }
        persist("I hereby declare this tax record to be true and complete.") { declaration =>
          taxAuthority ! declaration
        }
        persist(InvoiceRecord(latestInvoiceRecordId, recipient, date, amount)) { invoiceRecord =>
          taxAuthority ! invoiceRecord
          latestInvoiceRecordId += 1
        }
        persist("I hereby declare this tax record to be true and complete.") { declaration =>
          taxAuthority ! declaration
        }
    }

    override def receiveRecover: Receive = {
      case event => log.info(s"Recovered: $event")
    }

  }
  object DiligentAccountant {
    def props(taxId: String, taxAuthority: ActorRef) = Props(new DiligentAccountant(taxId, taxAuthority))
  }

  class TaxAuthority extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(s"Received: $message")
    }
  }

  val system = ActorSystem("MultiplePersistsDemo")
  val taxAuthority = system.actorOf(Props[TaxAuthority], "HMRC")
  val diligentAccountant = system.actorOf(DiligentAccountant.props("CDWQ0012", taxAuthority), "accountant")

  diligentAccountant ! Invoice("The Sofa Company", new Date, 2000)
  // The message ordering (TaxRecord -> InvoiceRecord) is GUARANTEED.
  // The TaxRecord callback is always called before the InvoiceRecord callback.
  /**
   * PERSISTENCE IS ALSO BASED ON MESSAGE PASSING.
   */

}
