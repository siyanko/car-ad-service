package controllers

import java.util.{Date, UUID}
import javax.inject.Inject

import data.Dynamo
import model._
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}


class Application @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  import CarAd._
  import Utils._
  import ApiActions._

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
      case None => NotFound(s"Could not find car advert with id: $id")
    }
  }

  def save = (CarAdAction(parse.tolerantJson) andThen validateAd) { req: CarAdRequest[JsValue] =>
    toCarAd(req) match {
      case Some(carAd) =>
        Dynamo.addCarAd(carAd)
        Ok(s"Your car advert has been successfully added with id: ${carAd.id}")

      case None => BadRequest("Could not proceed the request")
    }
  }

  def delete(id: String) = Action {
    Try(UUID.fromString(id)) match {
      case Success(uuid) =>
        Dynamo.deleteCarAd(uuid)
        Ok(s"Your car advert[id: $id] has been successfully deleted")

      case Failure(_) => BadRequest(s"Wrong car ad id: $id")
    }
  }
}
