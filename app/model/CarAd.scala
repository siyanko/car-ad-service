package model

import java.util.{Date, UUID}

import model.CarAd._
import play.api.libs.json.{JsValue, Json, Writes}

sealed trait CarAd

case class NewCar(id: Id,
                  title: Title,
                  fuel: Fuel,
                  price: Price) extends CarAd

case class UsedCar(id: Id,
                   title: Title,
                   fuel: Fuel,
                   price: Price,
                   mileage: Mileage,
                   firstRegistration: FirstRegistration) extends CarAd

object CarAd {

  type Id = UUID
  type Title = String
  type Price = Integer
  type Mileage = Integer
  type FirstRegistration = Date


  def apply(id: Id,
            title: Title,
            fuel: Fuel,
            price: Price): CarAd =
    NewCar(id, title, fuel, price)

  def apply(id: Id,
            title: Title,
            fuel: Fuel,
            price: Price,
            mileage: Mileage,
            firstRegistration: FirstRegistration): CarAd =
    UsedCar(id, title, fuel, price, mileage, firstRegistration)

  implicit val carAdWrites: Writes[CarAd] = new Writes[CarAd] {
    override def writes(c: CarAd): JsValue = c match {
      case NewCar(id, title, fuel, price) =>
        Json.obj(
          "id" -> id,
          "title" -> title,
          "fuel" -> fuel.sign,
          "price" -> price.toInt,
          "new" -> true
        )

      case UsedCar(id, title, fuel, price, mileage, firstRegistration) =>
        Json.obj(
          "id" -> id,
          "title" -> title,
          "fuel" -> fuel.sign,
          "price" -> price.toInt,
          "new" -> false,
          "mileage" -> mileage.toInt,
          "first_registration" -> firstRegistration.getTime
        )
    }
  }

}
