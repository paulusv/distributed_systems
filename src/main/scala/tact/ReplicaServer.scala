package tact

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

/**
  * ReplicaServer object.
  */
object ReplicaServer {

  def main(args: Array[String]): Unit = {


    implicit val system: ActorSystem = ActorSystem("tact-sever")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    implicit val replica: Replica = new Replica('A', "", "" :: Nil)

    val route =
      get {
        path("status") {
          complete(StatusCodes.OK)
        } ~
        pathPrefix("read" / Segment) { keyString =>
          if (!keyString.matches("[a-z]")) {
            complete(StatusCodes.BadRequest)
          } else {
            val key: Char = keyString.toList.head
            val maybeItem: Future[Option[Int]] = replica.read(key)

            onSuccess(maybeItem) {
              case Some(item) => complete(HttpEntity(ContentTypes.`application/json`, "{ \"key\": \"" + key + "\", \"value\": " + item + ", \"replica:\"" + replica.hashCode() + "}"))
              case None => complete(StatusCodes.NotFound)
            }
          }
        }
      } ~
      post {
        pathPrefix("write" / Segment) { keyString =>
          if (!keyString.matches("[a-z]")) {
            complete(StatusCodes.BadRequest)
          } else {
            val key: Char = keyString.toList.head

            entity(as[String]) { item =>
              val saved: Future[Done] = replica.write(key, item.toInt)
              onSuccess(saved) { _ =>
                complete(HttpEntity(ContentTypes.`application/json`, "{ \"key\": \"" + key + "\", \"message\": \"Value has been saved!\" }"))
              }
            }
          }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")

    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}