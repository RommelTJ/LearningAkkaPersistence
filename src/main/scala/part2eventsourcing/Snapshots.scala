package part2eventsourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object Snapshots extends App {

  // commands
  case class ReceivedMessage(contents: String) // message FROM your contact
  case class SentMessage(contents: String) // message TO your contact

  // events
  case class ReceivedMessageRecord(id: Int, contents: String)
  case class SentMessageRecord(id: Int, contents: String)

  class Chat(owner: String, contact: String) extends PersistentActor with ActorLogging {

    override def persistenceId: String = s"$owner-$contact-chat"

    override def receiveCommand: Receive = ???

    override def receiveRecover: Receive = ???

  }
  object Chat {
    def props(owner: String, contact: String) = Props(new Chat(owner, contact))
  }

  val system = ActorSystem("SnapshotsDemo")
  val chat = system.actorOf(Chat.props("rommel123", "daniel456"))

}
