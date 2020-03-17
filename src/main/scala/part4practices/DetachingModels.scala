package part4practices

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object DetachingModels extends App {

  case class User(id: String, email: String)
  case class Coupon(code: String, promotionAmount: Int)

  class CouponManager extends PersistentActor with ActorLogging {

  }

}
