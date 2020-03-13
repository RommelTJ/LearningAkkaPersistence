package part3storesandserialization

// command
case class RegisterUser(email: String, name: String)

// event
case class UserRegistered(id: Int, email: String, name: String)

object CustomSerialization extends App {

}
