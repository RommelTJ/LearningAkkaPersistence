package part2eventsourcing

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

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

    override def persistenceId: String = "simple-voting-station"

    override def receiveCommand: Receive = {
      case Vote(citizenPID, candidate) =>
        // 1 create the event
        // 2 persist the event
        // 3 handle the state change after persisting is successful
    }

    override def receiveRecover: Receive = ???

  }

}
