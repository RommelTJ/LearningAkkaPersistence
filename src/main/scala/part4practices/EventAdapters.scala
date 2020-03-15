package part4practices

object EventAdapters extends App {

  // Store for acoustic guitars

  // Data Structures
  case class Guitar(id: String, model: String, make: String)

  // Command
  case class AddGuitar(guitar: Guitar, quantity: Int)

  
}
