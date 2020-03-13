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

  override def toBinary(o: AnyRef): Array[Byte] = ???

  // manifest will be None since includeManifest is hardcoded to false
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = ???

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

}
