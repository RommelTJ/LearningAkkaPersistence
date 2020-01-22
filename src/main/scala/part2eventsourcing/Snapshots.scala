package part2eventsourcing

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object Snapshots extends App {

  class Chat(owner: String, contact: String) extends PersistentActor with ActorLogging {

    override def persistenceId: String = s"$owner-$contact-chat"

    override def receiveCommand: Receive = ???

    override def receiveRecover: Receive = ???

  }

}
