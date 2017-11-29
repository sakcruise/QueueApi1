package controllers

import javax.inject._

import models.{Clients, Country, Orders}
import play.api.libs.json.Json
import play.api.mvc._

import scala.util.Random



@Singleton
class QueueController @Inject() (cc: ControllerComponents) extends AbstractController(cc) {
  //Product Api
  def productApi(client: String) = Action {

    val v_products = List("EV", "GAS", "ELECTRICITY")
    val v_distinctClientList = client.split(",").toList.distinct

    implicit val ClientWrites = Json.writes[Clients]

    if(validateClientInput(v_distinctClientList)) {
      var v_ClientList = for (e <- v_distinctClientList)
                              yield e.last.toString.toInt match {
                              case x if x <= 3 => Clients(e.toInt, Random.shuffle(v_products).take(x))
                              case y if y > 3 => Clients(e.toInt, v_products ::: List.fill(y-3)(Random.shuffle(v_products).take(1).mkString))
        }

      val json = Json.toJson(v_ClientList)
      Ok(views.html.queue(json.toString))
      //Ok(json.toString)
      //Ok("Hello")
    }
    else
      NotFound(views.html.queue("There are invalid Client Id's"))

 }
  //Pricing Api
  def pricingApi(countryCode: String) = Action {

    val v_countryCode = countryCode.split(",").toList.distinct
    implicit val countryWrites = Json.writes[Country]

    if(validateCountryInput(v_countryCode)) {
      var v_CountryList = for (code <- v_countryCode)
                          yield Country(code.trim, Random.nextInt(100) * Random.nextFloat)

      val json = Json.toJson(v_CountryList)
      Ok(views.html.queue(json.toString))
    }
    else
      NotFound(views.html.queue("There are invalid Country Codes"))
  }
  // orderApi
  def orderApi(client: String) = Action {
     val v_orders = List("NEW","DELIVERED","ORDERING","SETUP")
     val v_distinctOrderList = client.split(",").toList.distinct
     implicit val OrdersWrites = Json.writes[Orders]

    if(validateClientInput(v_distinctOrderList)) {
      var v_OrderList = for (e <- v_distinctOrderList)
        yield Orders(e.toInt, Random.shuffle(v_orders).take(1).mkString(""))

      val json = Json.toJson(v_OrderList)
      Ok(views.html.queue(json.toString))
    }
    else
    NotFound(views.html.queue("There are invalid Client Id's"))
  }

  // to validate Input client id length must be 9 digits
  def validateClientInput(cl: List[String]): Boolean = {
    cl.filter(r => r.length != 9).length == 0
  }
  // to validate Input Country Code length must be 2 characters
  def validateCountryInput(cc: List[String]): Boolean = {
    cc.filter(r => r.length != 2).length == 0
  }

}
