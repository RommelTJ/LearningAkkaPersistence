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

}
