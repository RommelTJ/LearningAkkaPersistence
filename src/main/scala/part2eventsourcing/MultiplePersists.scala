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

    override def persistenceId: String = ???

    override def receiveCommand: Receive = ???

    override def receiveRecover: Receive = ???

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

}
