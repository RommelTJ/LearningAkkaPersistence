package part2eventsourcing

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

}
