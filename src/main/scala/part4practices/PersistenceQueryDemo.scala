package part4practices

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.query.PersistenceQuery
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

object PersistenceQueryDemo extends App {

  val system = ActorSystem("PersistenceQueryDemo", ConfigFactory.load().getConfig("persistenceQuery"))

  // Read Journal
  val readJournal = PersistenceQuery(system).readJournalFor[CassandraReadJournal](CassandraReadJournal.Identifier)

  // Persistence Ids query
  implicit val materializer: ActorMaterializer = ActorMaterializer()(system)
  val persistenceIds = readJournal.persistenceIds()
  persistenceIds.runForeach(persistenceId => println(s"Found persistence Id: $persistenceId"))

  class SimplePersistentActor extends PersistentActor with ActorLogging {
    override def persistenceId: String = "persistence-query-id-1"

    override def receiveCommand: Receive = {
      case m => persist(m) { _ => log.info(s"Persisted $m")}
    }

    override def receiveRecover: Receive = {
      case e => log.info(s"Recovered $e")
    }
  }

  val simpleActor = system.actorOf(Props[SimplePersistentActor], "simplePersistentActor")

  import system.dispatcher
  system.scheduler.scheduleOnce(5 seconds) {
    val message = "Hello World"
    simpleActor ! message
  }
 
}

