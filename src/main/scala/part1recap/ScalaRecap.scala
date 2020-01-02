package part1recap

import scala.concurrent.Future
import scala.util.{Failure, Success}

object ScalaRecap extends App {

  val aCondition: Boolean = false
  def myFunction(x: Int): Int = {
    // code
    if (x > 4) 42 else 65
  }
  // instructions vs expressions
  // types + type inference

  // OO features of Scala
  class Animal
  trait Carnivore {
    def eat(a: Animal): Unit
  }

  object Carnivore

  // Generics
  abstract class MyList[+A]

  // Method Notations
  1 + 2 // infix notation
  1.+(2)

  // Functional Programming
  // val anIncrementer: Function1[Int, Int]
  val anIncrementer: Int => Int = (x: Int) => x + 1
  anIncrementer(1)

  List(1, 2, 3).map(anIncrementer)
  // HOF: Map, flatMap, filter
  // For-comprehensions, chains of map, flatmap, filter

  // Monads: Option, Try

  // Pattern matching
  val unknown: Any = 2
  val order = unknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "unknown"
  }

  try {
    // code that can throw an exception
    throw new RuntimeException
  } catch {
    case e: Exception => println("I caught one!")
  }

  /**
   * Scala advanced features
   */
  // Multithreading
  import scala.concurrent.ExecutionContext.Implicits.global
  val future = Future {
    // long computation here
    // executed on SOME other thread
    42
  }
  // map, flatMap, filter + other niceties e.g. recover / recoverWith

  future.onComplete {
    case Success(value) => println(s"I found the meaning of life: $value")
    case Failure(exception) => println(s"I found $exception while searching for the meaning of life.")
  } // executed on SOME thread

  // partial functions
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 65
    case _ => 999
  } // based on Pattern Matching

  // Type aliases
  type AkkaReceive = PartialFunction[Any, Unit]
  def receive: AkkaReceive = {
    case 1 => println("Hello")
    case _ => println("Confused...")
  }

}
