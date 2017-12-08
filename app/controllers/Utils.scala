package controllers

import play.api.libs.json.{Json, Writes}

object Utils {

  implicit class JsonConvertions[A](data: A) {
    def toJson (implicit writes: Writes[A]) = Json.toJson(data)
  }

}
