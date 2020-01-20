package part2eventsourcing

import java.util.Date

object MultiplePersists extends App {

  /**
   * Diligent Account
   * With every invoice, this actor will persist TWO events.
   * - a tax record for the fiscal authority
   * - an invoice record for personal logs or some auditing authority
   */

  // Command
  case class Invoice(recipient: String, date: Date, amount: Int)

}
