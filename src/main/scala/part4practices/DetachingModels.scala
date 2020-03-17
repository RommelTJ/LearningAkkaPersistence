package part4practices

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

import scala.collection.mutable

object DetachingModels extends App {

  case class User(id: String, email: String)
  case class Coupon(code: String, promotionAmount: Int)

  // Command
  case class ApplyCoupon(coupon: Coupon, user: User)

  // Event
  case class CouponApplied(code: String, user: User)

  class CouponManager extends PersistentActor with ActorLogging {
    val coupons: mutable.Map[String, User] = new mutable.HashMap[String, User]()

    override def persistenceId: String = "coupon-manager"

    override def receiveCommand: Receive = {
      case ApplyCoupon(coupon, user) =>
        if (!coupons.contains(coupon.code)){
          persist(CouponApplied(coupon.code, user)) { e =>
            log.info(s"Persisted $e")
            coupons.put(coupon.code, user)
          }
        }
    }

    override def receiveRecover: Receive = {
      case event @ CouponApplied(code, user) =>
        log.info(s"Recovered $event")
        coupons.put(code, user)
    }

  }

}
