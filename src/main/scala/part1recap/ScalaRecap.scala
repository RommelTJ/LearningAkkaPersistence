package part1recap

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

}
