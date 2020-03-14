package part3storesandserialization

import akka.actor.ActorLogging
import akka.persistence.PersistentActor
import akka.serialization.Serializer

// command
case class RegisterUser(email: String, name: String)

// event
case class UserRegistered(id: Int, email: String, name: String)

// serializer
class UserRegistrationSerializer extends Serializer {
  override def identifier: Int = 43272
  val SEPARATOR = "//"

  override def toBinary(o: AnyRef): Array[Byte] = {
    case event @ UserRegistered(id, email, name) =>
      println(s"Serializing $event")
      s"[$id$SEPARATOR$email$SEPARATOR$name]".getBytes
    case _ =>
      throw new IllegalArgumentException("Only user registration events supported in this serializer")
  }

  // manifest will be None since includeManifest is hardcoded to false
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val string = new String(bytes)
    val values = string.substring(1, string.length - 1).split(SEPARATOR)
    val id = values(0).toInt
    val email = values(1)
    val name = values(2)

    val result = UserRegistered(id, email, name)
    println(s"Deserialized $string to $result")

    result
  }

  override def includeManifest: Boolean = false
}

// actor
class UserRegistrationActor extends PersistentActor with ActorLogging {
  var currentId = 0

  override def persistenceId: String = "user-registration"

  override def receiveCommand: Receive = {
    case RegisterUser(email, name) =>
      persist(UserRegistered(currentId, email, name)) { e =>
        currentId += 1
        log.info(s"Persisted: $e")
      }
  }

  override def receiveRecover: Receive = {
    case event @ UserRegistered(id, _, _) =>
      log.info(s"Recovered: $event")
      currentId = id
  }
}

object CustomSerialization extends App {

  /**
   * Send Command to the Actor
   * - Actor calls persist
   * - Serializer serializes the event into bytes
   * - The journal writes the bytes
   */

}
