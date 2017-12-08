package model


object Fuel{
  def apply(sign: String): Fuel = sign match {
    case "D" => Diesel
    case "G" => Gasoline
  }
}

sealed trait Fuel{
  def sign: String
}

case object Gasoline extends Fuel{
  override def sign: String = "G"
}

case object Diesel extends Fuel {
  override def sign: String = "D"
}
