package part4practices

import akka.actor.{ActorLogging, ActorSystem, Props}
import akka.persistence.PersistentActor
import akka.persistence.journal.{EventAdapter, EventSeq}
import com.typesafe.config.ConfigFactory
import part4practices.DomainModel.CouponApplied

import scala.collection.mutable

object DetachingModels extends App {

  class CouponManager extends PersistentActor with ActorLogging {
    import DomainModel._

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

  val system = ActorSystem("DetachingModels", ConfigFactory.load().getConfig("detachingModels"))
  val couponManager = system.actorOf(Props[CouponManager], "couponManager")

  import DomainModel._
//  for (i <- 1 to 5) {
//    val coupon = Coupon(s"MEGA COUPON_$i", 100)
//    val user = User(s"$i", s"user_$i@rjtvm.com")
//
//    couponManager ! ApplyCoupon(coupon, user)
//  }

}

object DomainModel {
  case class User(id: String, email: String, name: String)
  case class Coupon(code: String, promotionAmount: Int)

  // Command
  case class ApplyCoupon(coupon: Coupon, user: User)

  // Event
  case class CouponApplied(code: String, user: User)
}

object DataModel {
  case class WrittenCouponApplied(code: String, userId: String, userEmail: String)
}

class ModelAdapter extends EventAdapter {
  import DomainModel._
  import DataModel._

  override def manifest(event: Any): String = "CMA"

  // journal -> serializer -> fromJournal -> actor
  override def fromJournal(event: Any, manifest: String): EventSeq = {
    event match {
      case event @ WrittenCouponApplied(code, userId, userEmail) =>
        println(s"Converting $event to domain model")
        EventSeq.single(CouponApplied(code, User(userId, userEmail, "")))
      case other =>
        EventSeq.single(other)
    }
  }

  // actor -> toJournal -> serializer -> journal
  override def toJournal(event: Any): Any = {
    event match {
      case event @ CouponApplied(code, user) =>
        println(s"Converting $event to data model")
        WrittenCouponApplied(code, user.id, user.email)
    }
  }

}
