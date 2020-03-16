package part4practices

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor
import com.typesafe.config.ConfigFactory

import scala.collection.mutable

object EventAdapters extends App {

  // Store for acoustic guitars

  // Data Structures
  case class Guitar(id: String, model: String, make: String)

  // Command
  case class AddGuitar(guitar: Guitar, quantity: Int)

  // Event
  case class GuitarAdded(guitarId: String, guitarModel: String, guitarMake: String, quantity: Int)

  class InventoryManager extends PersistentActor with ActorLogging {
    val inventory: mutable.Map[Guitar, Int] = new mutable.HashMap[Guitar, Int]()

    override def persistenceId: String = "guitar-inventory-manager"

    override def receiveCommand: Receive = {
      case AddGuitar(guitar @ Guitar(id, model, make), quantity) =>
        persist(GuitarAdded(id, model, make, quantity)) { _ =>
          addGuitarInventory(guitar, quantity)
          log.info(s"Added $quantity x $guitar to inventory")
        }
      case "print" =>
        log.info(s"Current inventory is: $inventory")
    }

    override def receiveRecover: Receive = {
      case event @ GuitarAdded(id, model, make, quantity) =>
        log.info(s"Recovered $event")
        val guitar = Guitar(id, model, make)
        addGuitarInventory(guitar, quantity)
    }

    def addGuitarInventory(guitar: Guitar, quantity: Int): Option[Int] = {
      val existingQuantity = inventory.getOrElse(guitar, 0)
      inventory.put(guitar, existingQuantity + quantity)
    }
  }

  val system = ActorSystem("eventAdapters", ConfigFactory.load().getConfig("eventAdapters"))
  val inventoryManager = system.actorOf(Props[InventoryManager], "inventoryManager")

  val guitars = for (i <- 1 to 10) yield Guitar(s"$i", s"Hakker$i", "RTJVM")
  guitars.foreach(g => {
    inventoryManager ! AddGuitar(g, 5)
  })

}
