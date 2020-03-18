package part4practices

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object PersistenceQueryDemo extends App {

  val system = ActorSystem("PersistenceQueryDemo", ConfigFactory.load().getConfig("persistenceQuery"))

}
