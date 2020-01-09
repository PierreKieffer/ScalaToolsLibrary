
import java.io.{File, FileOutputStream}

import akka.Done
import akka.actor.CoordinatedShutdown
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, Multipart, StatusCodes}
import akka.http.scaladsl.server.Directives.{entity, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.Credentials
import akka.stream.scaladsl.Sink
import akka.util.ByteString

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.{Failure, Success}

object AkkaWebServer extends utils.AkkaActor {
  def main(args: Array[String]): Unit = {
    initializeWebServer("localhost",8080)
  }

    /*
    Application example of upload and download files on Akka Http server side.

    Upload :
    curl -i -X POST -H "Authorization: Bearer YWtrYS1odHRwLWF1dGgK" -H "Content-Type: multipart/form-data" -F
    "data=@test.zip" http://localhost:8080/upload

    Download :
    curl -H "Authorization: Bearer YWtrYS1odHRwLWF1dGgK" --output file.zip
    http://localhost:8080/download?fileName="file.zip"
     */

  val secret : String = "YWtrYS1odHRwLWF1dGgK" //base64 secret just for example

  def checkAuthentication(credentials: Credentials): Option[String] = credentials match {
    case p @ Credentials.Provided(token) if p.verify(secret) => Some(token)
    case _ => None
  }

  def initializeWebServer(interface : String, port : Int) : Unit = {
    val route: Route = {
      authenticateOAuth2(realm = "secure site", checkAuthentication){ token =>
        concat (
          post{
            /*
            Route to upload a file
            */
            path("upload"){
              entity(as[Multipart.FormData]){fileData =>
                val filePath = "/path to store/uploadFile/"
                val processFileFuture = processFile(filePath,fileData)
                onComplete(processFileFuture){
                  case Success(Done)  => complete(HttpResponse(StatusCodes.OK,entity = s"File successfully uploaded"))
                  case Failure(ex) => complete(StatusCodes.InternalServerError -> ex.toString)
                }
              }

            }
          },
          get {
            /*
            Route to download a file
             */
            path("download"){
              parameters('fileName){
                (fileName) => {
                  val filePath = s"/path to store/uploadFile/$fileName"
                  complete(HttpEntity.fromFile(ContentTypes.`application/octet-stream`, new File(filePath)))
                }
              }
            }
          }
        )
      }
    }

    val bindingFuture = Http().bindAndHandle(route, interface, port.toInt)
    println(s"Server online at http://$interface:$port/")

    CoordinatedShutdown(system).addJvmShutdownHook({
      bindingFuture
        .flatMap(_.unbind())
    })

  }

  def processFile(filePath: String, fileData: Multipart.FormData) : Future[Done] = {
    fileData.parts.mapAsync(1) { bodyPart =>
      val fileOutput = new FileOutputStream(filePath + bodyPart.filename.get)
      def writeFileOnLocal(unit: Unit, byteString: ByteString): Unit= {
        val byteArray: Array[Byte] = byteString.toArray
        fileOutput.write(byteArray)
      }
      bodyPart.entity.withoutSizeLimit().dataBytes.runFold()(writeFileOnLocal)
    }
    }.runWith(Sink.ignore)
}
