package part2eventsourcing

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

import scala.collection.mutable

object PersistentActorsExercise extends App {

  /**
   * Persistent Actor for a voting station.
   * Keep:
   *  - the citizens who voted
   *  - the poll: mapping between a candidate and the number of received votes so far
   *
   * The actor must be able to recover its state if it's shut down or restarted.
   */
  case class Vote(citizenPID: String, candidate: String)

  class VotingStation extends PersistentActor with ActorLogging {

    // Ignore the mutable state for now.
    val citizens: mutable.Set[String] = new mutable.HashSet[String]
    val poll: mutable.Map[String, Int] = new mutable.HashMap[String, Int]()

    override def persistenceId: String = "simple-voting-station"

    override def receiveCommand: Receive = {
      case vote @ Vote(citizenPID, candidate) =>
        if (!citizens.contains(vote.citizenPID)) {
          // 1 create the event
          // 2 persist the event
          persist(vote) { _ => // COMMAND sourcing
            // 3 handle the state change after persisting is successful
            log.info(s"Persisted: $vote")
            handleInternalStateChange(citizenPID, candidate)
          }
        }
      case "print" => log.info(s"Current state: ")
    }

    def handleInternalStateChange(citizenPID: String, candidate: String): Unit = {
      citizens.add(citizenPID)
      val votes = poll.getOrElse(candidate, 0)
      poll.put(candidate, votes + 1)
    }

    override def receiveRecover: Receive = ???

  }

}
