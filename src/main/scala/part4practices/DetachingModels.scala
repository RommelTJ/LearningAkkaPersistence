package part4practices

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

object DetachingModels extends App {

  case class User(id: String, email: String)
  case class Coupon(code: String, promotionAmount: Int)

  // Command
  case class ApplyCoupon(coupon: Coupon, user: User)

  // Event
  case class CouponApplied(code: String, user: User)

  class CouponManager extends PersistentActor with ActorLogging {

  }

}
