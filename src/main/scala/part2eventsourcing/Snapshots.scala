package part2eventsourcing

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.{PersistentActor, SaveSnapshotFailure, SaveSnapshotSuccess, SnapshotOffer}

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
    var commandsWithoutCheckpoint = 0

    override def persistenceId: String = s"$owner-$contact-chat"

    override def receiveCommand: Receive = {
      case ReceivedMessage(contents) =>
        persist(ReceivedMessageRecord(currentMessageId, contents)) { e =>
          log.info(s"Received message: $contents")
          maybeReplaceMessage(contact, contents)
          currentMessageId += 1
          maybeCheckpoint()
        }
      case SentMessage(contents) =>
        persist(SentMessageRecord(currentMessageId, contents)) { e =>
          log.info(s"Sent message: $contents")
          maybeReplaceMessage(owner, contents)
          currentMessageId += 1
          maybeCheckpoint()
        }
      case "print" =>
        log.info(s"Most recent messages: $lastMessages")
      // snapshot-related messages
      case SaveSnapshotSuccess(metadata) => log.info(s"Saving snapshot succeeded: $metadata")
      case SaveSnapshotFailure(metadata, reason) => log.info(s"Saving snapshot $metadata failed because of $reason")
    }

    override def receiveRecover: Receive = {
      case ReceivedMessageRecord(id, contents) =>
        log.info(s"Recovered received message with id: $id and contents: $contents")
        maybeReplaceMessage(contact, contents)
        currentMessageId = id
      case SentMessageRecord(id, contents) =>
        log.info(s"Recovered sent message with id: $id and contents: $contents")
        maybeReplaceMessage(owner, contents)
        currentMessageId = id
      case SnapshotOffer(metadata, contents) =>
        log.info(s"Recovered snapshot: $metadata")
        contents.asInstanceOf[mutable.Queue[(String, String)]].foreach(lastMessages.enqueue(_))
    }

    def maybeReplaceMessage(sender: String, contents: String): Unit = {
      if (lastMessages.size >= MAX_MESSAGES) {
        lastMessages.dequeue()
      }
      lastMessages.enqueue((sender, contents))
    }

    def maybeCheckpoint(): Unit = {
      commandsWithoutCheckpoint += 1
      if (commandsWithoutCheckpoint >= MAX_MESSAGES) {
        log.info(s"Saving checkpoint...")
        saveSnapshot(lastMessages) // asynchronous operation
        commandsWithoutCheckpoint = 0
      }
    }

  }
  object Chat {
    def props(owner: String, contact: String) = Props(new Chat(owner, contact))
  }

  val system = ActorSystem("SnapshotsDemo")
  val chat = system.actorOf(Chat.props("rommel123", "daniel456"))

//  for (i <- 1 to 100000) {
//    chat ! ReceivedMessage(s"Akka Rocks $i")
//    chat ! SentMessage(s"Akka Rules $i")
//  }

  // Snapshots!
  chat ! "print"

  /**
   * event1
   * event2
   * event3
   * snapshot1
   * event4
   * snapshot2
   * event5
   * event6
   *
   * When messages are recovered, it only recovers messages since the last snapshot!
   */

  /**
   * pattern:
   * - after each persist, maybe save a snapshot (logic is up to you)
   * - if you save a snapshot, you should handle the SnapshotOffer message in receiveRecover
   * - (optional, but best practice) you should handle the SaveSnapshotSuccess and SaveSnapshotFailure messages in receiveCommand
   * - ???
   * - profit from the extra speed
   */

}
