package controllers

import javax.inject.Inject

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents}

sealed trait InOut[A]
case class PrintlnLine(line: String) extends InOut[Unit]
case class AllCarAds(ls: List[String]) extends InOut[JsValue]

class Application @Inject()(cc: ControllerComponents)
  extends AbstractController(cc) {

  val carAds = List(
    "Slavuta 2006",
    "VW 2007",
    "BMW 2008",
    "Mercedes 2009"
  )

  def index() = Action {
    Ok("Welcome to funcy car-ad-service")
  }


  def allCarAds() = Action {
    println("Log... getting all car ads from database...")
    val json: JsValue = Json.toJson(carAds)
    println(s"Log.... returning all car ads $json")
    Ok(json)
  }

}
