package part4practices

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object DetachingModels extends App {

  case class User(id: String, email: String)

  class CouponManager extends PersistentActor with ActorLogging {

  }

}
