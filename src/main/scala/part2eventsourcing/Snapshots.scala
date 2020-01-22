package part2eventsourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

import scala.collection.mutable

object Snapshots extends App {

  // commands
  case class ReceivedMessage(contents: String) // message FROM your contact
  case class SentMessage(contents: String) // message TO your contact

  // events
  case class ReceivedMessageRecord(id: Int, contents: String)
  case class SentMessageRecord(id: Int, contents: String)

  class Chat(owner: String, contact: String) extends PersistentActor with ActorLogging {

    val MAX_MESSAGES = 10

    var currentMessageId = 0
    val lastMessages = new mutable.Queue[(String, String)]()

    override def persistenceId: String = s"$owner-$contact-chat"

    override def receiveCommand: Receive = {
      case ReceivedMessage(contents) =>
        persist(ReceivedMessageRecord(currentMessageId, contents)) { e =>
          log.info(s"Received message: $contents")
          if (lastMessages.size >= MAX_MESSAGES) {
            lastMessages.dequeue()
          }
          lastMessages.enqueue((contact, contents))

          currentMessageId += 1
        }
      case SentMessage(contents) =>
        persist(SentMessageRecord(currentMessageId, contents)) { e =>
          log.info(s"Sent message: $contents")
          if (lastMessages.size >= MAX_MESSAGES) {
            lastMessages.dequeue()
          }
          lastMessages.enqueue((owner, contents))

          currentMessageId += 1
        }
    }

    override def receiveRecover: Receive = ???

  }
  object Chat {
    def props(owner: String, contact: String) = Props(new Chat(owner, contact))
  }

  val system = ActorSystem("SnapshotsDemo")
  val chat = system.actorOf(Chat.props("rommel123", "daniel456"))

}
