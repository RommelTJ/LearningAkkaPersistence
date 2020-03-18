package part4practices

import akka.actor.ActorSystem
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object PersistenceQueryDemo extends App {

  val system = ActorSystem("PersistenceQueryDemo", ConfigFactory.load().getConfig("persistenceQuery"))

  // Read Journal
  val readJournal = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  // Persistence Ids query
  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)
  val persistenceIds = readJournal.persistenceIds()
  persistenceIds.runForeach(persistenceId => println(s"Found persistence Id: $persistenceId"))

}
