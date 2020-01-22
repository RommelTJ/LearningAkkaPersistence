package part2eventsourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor

object Snapshots extends App {

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
