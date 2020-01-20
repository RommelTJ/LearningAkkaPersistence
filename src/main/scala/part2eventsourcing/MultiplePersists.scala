package part2eventsourcing

import java.util.Date

import akka.actor.{ActorLogging, ActorRef}
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

}
