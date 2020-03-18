package part4practices

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor
import akka.persistence.journal.{EventSeq, ReadEventAdapter}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable

object EventAdapters extends App {

  // Store for acoustic guitars
  val ACOUSTIC = "acoustic"
  val ELECTRIC = "electric"

  // Data Structures
  case class Guitar(id: String, model: String, make: String, guitarType: String = ACOUSTIC)

  // Command
  case class AddGuitar(guitar: Guitar, quantity: Int)

  // Event
  case class GuitarAdded(guitarId: String, guitarModel: String, guitarMake: String, quantity: Int)
  case class GuitarAddedV2(guitarId: String, guitarModel: String, guitarMake: String, quantity: Int, guitarType: String)

  class InventoryManager extends PersistentActor with ActorLogging {
    val inventory: mutable.Map[Guitar, Int] = new mutable.HashMap[Guitar, Int]()

    override def persistenceId: String = "guitar-inventory-manager"

    override def receiveCommand: Receive = {
      case AddGuitar(guitar @ Guitar(id, model, make, guitarType), quantity) =>
        persist(GuitarAddedV2(id, model, make, quantity, guitarType)) { _ =>
          addGuitarInventory(guitar, quantity)
          log.info(s"Added $quantity x $guitar to inventory")
        }
      case "print" =>
        log.info(s"Current inventory is: $inventory")
    }

    override def receiveRecover: Receive = {
      case event @ GuitarAddedV2(id, model, make, quantity, guitarType) =>
        log.info(s"Recovered $event")
        val guitar = Guitar(id, model, make, guitarType)
        addGuitarInventory(guitar, quantity)
    }

    def addGuitarInventory(guitar: Guitar, quantity: Int): Option[Int] = {
      val existingQuantity = inventory.getOrElse(guitar, 0)
      inventory.put(guitar, existingQuantity + quantity)
    }
  }

  class GuitarReadEventAdapter extends ReadEventAdapter {
    /**
     * journal -> serializer -> read event adapter -> actor
     * (bytes)    (GuitarAdded) (GuitarAddedV2)       (receiveRecover)
     */
    override def fromJournal(event: Any, manifest: String): EventSeq = {
      event match {
        case GuitarAdded(id, model, make, quantity) =>
          EventSeq.single(GuitarAddedV2(id, model, make, quantity, ACOUSTIC))
        case other => EventSeq.single(other)
      }
    }
  }
  // WriteEventAdapter
  // actor -> write event adapter -> serializer -> journal
  // EventAdapter (contains toJournal)

  val system = ActorSystem("eventAdapters", ConfigFactory.load().getConfig("eventAdapters"))
  val inventoryManager = system.actorOf(Props[InventoryManager], "inventoryManager")

//  val guitars = for (i <- 1 to 10) yield Guitar(s"$i", s"Hakker$i", "RTJVM")
//  guitars.foreach(g => {
//    inventoryManager ! AddGuitar(g, 5)
//  })

  inventoryManager ! "print"

}
