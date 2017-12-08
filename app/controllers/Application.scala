package controllers

import java.util.{Date, UUID}
import javax.inject.Inject

import data.Dynamo
import model._
import play.api.mvc.{AbstractController, ControllerComponents}


class Application @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  import CarAd._
  import Utils._

  def index() = Action {
    Ok("Welcome to funcy car-advert-service")
  }

  def fillTestData() = Action {
    val cars: List[CarAd] = List(
      CarAd(UUID.randomUUID(), "BMW X1", Gasoline, 32000),
      CarAd(UUID.randomUUID(), "VW Golf", Diesel, 5000, 213001, new Date()),
      CarAd(UUID.randomUUID(), "VW Polo", Gasoline, 2500, 323012, new Date())
    )

    Dynamo.createCarAds(cars)

    Ok("Test data have been created")
  }


  def allCarAds() = Action {
    Ok(Dynamo.retrieveCarAds.toJson)
  }

  def getCarAd(id: String) = Action {
    Dynamo.getCarAd(id) match {
      case Some(car) => Ok(car.toJson)
      case None => BadRequest(s"Could not find car advert with id: $id")
    }
  }


}
