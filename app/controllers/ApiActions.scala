package controllers

import java.util.{Date, UUID}

import model.{CarAd, Fuel}
import play.api.libs.json.JsObject
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

object ApiActions {
  case class CarAdRequest[A](val title: Option[String] = None,
                             val fuel: Option[String] = None,
                             val price: Option[Int] = None,
                             val isNew: Option[Boolean] = None,
                             val mileage: Option[Int] = None,
                             val firstRegistration: Option[Long] = None,
                             request: Request[A]
                            ) extends WrappedRequest(request)

  def CarAdAction[A](bodyParser: BodyParser[A])(implicit ec: ExecutionContext) =
    new ActionBuilder[CarAdRequest, A] with ActionTransformer[Request, CarAdRequest] {

      override def parser: BodyParser[A] = bodyParser

      override protected def transform[A](request: Request[A]): Future[CarAdRequest[A]] = Future.successful {
        request.body match {
          case js@JsObject(_) => CarAdRequest(
            (js \ "title").asOpt[String],
            (js \ "fuel").asOpt[String],
            (js \ "price").asOpt[Int],
            (js \ "new").asOpt[Boolean],
            (js \ "mileage").asOpt[Int],
            (js \ "first_registration").asOpt[Long],
            request
          )

          case _ => CarAdRequest(request = request)
        }
      }

      override protected def executionContext: ExecutionContext = ec
    }


  def validateAd(implicit ec: ExecutionContext) = new ActionFilter[CarAdRequest] {
    override protected def filter[A](request: CarAdRequest[A]): Future[Option[Result]] = Future.successful {
      request match {
        case CarAdRequest(Some(_), Some(_), Some(_), Some(true), _, _, _) => None
        case CarAdRequest(Some(_), Some(_), Some(_), Some(false), Some(_), Some(_), _) => None
        case _ => Some(BadRequest("Missing one/all required parameters in the request"))
      }
    }

    override protected def executionContext: ExecutionContext = ec
  }

  def toCarAd[A](req: CarAdRequest[A]): Option[CarAd] = req match {
    case CarAdRequest(Some(ttl), Some(f), Some(p), Some(true), _, _, _) =>
      Some(CarAd(UUID.randomUUID(), ttl, Fuel(f), p))

    case CarAdRequest(Some(ttl), Some(f), Some(p), Some(false), Some(m), Some(fr), _) =>
      Some(CarAd(UUID.randomUUID(), ttl, Fuel(f), p, m, new Date(fr)))

    case _ => None
  }

}
