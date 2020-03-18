package part4practices

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor
import akka.persistence.cassandra.query.scaladsl.CassandraReadJournal
import akka.persistence.journal.{Tagged, WriteEventAdapter}
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
  val persistenceIds = readJournal.persistenceIds() // Infinite stream
  // readJournal.currentPersistenceIds() // Finite stream
//  persistenceIds.runForeach(persistenceId => println(s"Found persistence Id: $persistenceId"))

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
    val message = "Hello a second time"
    simpleActor ! message
  }

  // Events by Persistence Id
  val events = readJournal.eventsByPersistenceId("persistence-query-id-1", 0, Long.MaxValue)
  // readJournal.currentEventsByPersistenceId() // Finite version
  events.runForeach(e => println(s"Read event: $e"))

  // events by tags
  val genres = Array("pop", "rock", "hip-hop", "jazz", "disco")
  case class Song(artist: String, title: String, genre: String)
  case class Playlist(songs: List[Song]) // command
  case class PlaylistPurchased(id: Int, songs: List[Song]) // event

  class MusicStoreCheckoutActor extends PersistentActor with ActorLogging {
    var latestPlaylistId = 0
    override def persistenceId: String = "music-store-checkout"

    override def receiveCommand: Receive = {
      case Playlist(songs) =>
        persist(PlaylistPurchased(latestPlaylistId, songs)) { _ =>
          log.info(s"User purchased: $songs")
          latestPlaylistId += 1
        }
    }

    override def receiveRecover: Receive = {
      case event @ PlaylistPurchased(id, _) =>
        log.info(s"Recovered: $event")
        latestPlaylistId = id
    }
  }

  class MusicStoreEventAdapter extends WriteEventAdapter {
    override def manifest(event: Any): String = "musicStore"

    override def toJournal(event: Any): Any = {
      event match {
        case event @ PlaylistPurchased(_, songs) =>
          val genres = songs.map(_.genre).toSet
          Tagged(event, genres)
      }
    }
  }

}

