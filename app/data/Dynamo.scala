package data

import java.util.{Date, UUID}

import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.amazonaws.services.dynamodbv2.document.{DeleteItemOutcome, DynamoDB, Item, PutItemOutcome}
import model.CarAd.Id
import model.{CarAd, Fuel, NewCar, UsedCar}

import scala.collection.JavaConverters._

object Dynamo {

  private lazy val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder
    .standard()
    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", Regions.DEFAULT_REGION.getName))
    .build()

  private lazy val dynamoDB: DynamoDB = new DynamoDB(client)

  private lazy val tableName: String = "car-ads"

  private lazy val putItem: Item => PutItemOutcome = item =>
    dynamoDB
      .getTable(tableName)
      .putItem(item)

  private lazy val createItem: CarAd => Item = _ match {
    case NewCar(id, title, fuel, price) =>
      new Item()
        .withPrimaryKey("Id", id.toString)
        .withString("Title", title)
        .withString("fuel", fuel.sign)
        .withNumber("Price", price)
        .withBoolean("new", true)

    case UsedCar(id, title, fuel, price, mileage, firstRegistration) =>
      new Item()
        .withPrimaryKey("Id", id.toString)
        .withString("Title", title)
        .withString("fuel", fuel.sign)
        .withNumber("Price", price)
        .withBoolean("new", false)
        .withNumber("mileage", mileage)
        .withNumber("first_registration", firstRegistration.getTime)
  }

  private lazy val createCarAd: Item => CarAd = item => item.getBoolean("new") match {
    case true =>
      CarAd(UUID.fromString(item.getString("Id")), item.getString("Title"), Fuel(item.getString("fuel")), item.getInt("Price"))

    case false =>
      CarAd(
        UUID.fromString(item.getString("Id")),
        item.getString("Title"),
        Fuel(item.getString("fuel")),
        item.getInt("Price"),
        item.getInt("mileage"),
        new Date(item.getLong("first_registration"))
      )
  }

  lazy val createCarAds: List[CarAd] => Unit = list => list.foreach(createItem andThen putItem)

  lazy val addCarAd: CarAd => PutItemOutcome = createItem andThen putItem

  def retrieveCarAds: Array[CarAd] = dynamoDB.getTable(tableName).scan(new ScanSpec()).asScala.map(createCarAd).toArray

  lazy val getCarAd: String => Option[CarAd] = id => Option {
    dynamoDB.getTable(tableName).getItem("Id", id)
  }.map(createCarAd)

  lazy val deleteCarAd: Id => DeleteItemOutcome = id => dynamoDB.getTable(tableName).deleteItem("Id", id.toString)

}
