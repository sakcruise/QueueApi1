import akka.actor._
import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import com.sun.deploy.config._
import akka.stream._
import akka.stream.scaladsl._

import akka.pattern
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure

object queue {

  def main(args: Array[String]): Unit = {

    class ProcessApi extends Actor {
         def receive = {
            case (x: String,y: List[String]) => { val response = forwardRequestApi(x, y)
                                                   sender ! response
            }

         }
    }

    def processQueueApi(listApi: List[(String, List[String])]): Unit = {

      for (e <- listApi) {
        QueueApi(e._1, e._2)

        /*
        -----------------------------------------
        code for throttling and queuing using akka streams integrating with Actor models
//
        val system = ActorSystem("ProcessApi")
        implicit val materializer = ActorMaterializer()
        implicit val executionContext = system.dispatcher
          val actor = system.actorOf(Props(classOf[ProcessApi], materializer))


        implicit val timeout = Timeout(10.seconds)
         val response = Source(listApi)
          .throttle(
          elements = 5,
          per = 1 second,
          maximumBurst = 100,
          mode = ThrottleMode.shaping
        )
        .map { _ =>
          actor ! (e._1, e._2)
        }
        .runWith(Sink.ignore)

        println(response)
        ----------------------------------------

        */

        val system = ActorSystem("ProcessApi")
        val actor = system.actorOf(Props[ProcessApi], "ProcessApiActor")
        implicit val timeout = Timeout(10.seconds)
        val response = actor ? (e._1, e._2)

        /* Code for scheduling every 5 seconds
        //system.scheduler.schedule(5.seconds, 5.seconds, actor, (e._1,e._2))
        */
        val result = Await.result(response, timeout.duration).asInstanceOf[String]
        println(result)
      }
    }

      //Getting the response for different Api
    def forwardRequestApi(m: String, q: List[String]): String = {
        val url = "http://localhost:9000/"+m.trim+"?q=" + q.mkString(",").trim
        val response = scala.io.Source.fromURL(url).mkString
        response
    }
        /* Code using Apache Http Response
//        ......
//        val response: HttpResponse[String] = Http(url)
//        .timeout(connTimeoutMs = 50000, readTimeoutMs = 50000)
//        .param("format", "json")
//        .asString
//
//        val body = response.body
//        println(body)
//        body
//
//
//        val get = new HttpGet(url)
//        val client = new HttpClientBuilder
//        val response = client(get)
      */


    /* Test Data */
     processQueueApi(List(("order", List("123456783","123456781","123456789")),("clients", List("123456783","123456781","123456789")),("pricing", List("NL","GB")),("clients", List("123456781")), ("pricing", List("NL","GB")), ("pricing", List("NL","GB")), ("pricing", List("NL","GB")), ("pricing", List("NL","GB"))))

  }


  }
