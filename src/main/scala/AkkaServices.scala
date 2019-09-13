
import java.net.InetSocketAddress
import java.time.{Instant, LocalDateTime, LocalTime, Period}

import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}

import scala.concurrent.Await
import org.apache.spark.sql.functions._
import akka.http.scaladsl.{ClientTransport, Http}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, headers}
import akka.http.scaladsl.settings.{ClientConnectionSettings, ConnectionPoolSettings}
import akka.http.scaladsl.unmarshalling.Unmarshal
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.col
import spray.http.HttpHeaders.Cookie
import utils.AkkaActor

import scala.concurrent.duration._
import akka.http.scaladsl.model._

import scala.concurrent.ExecutionContext.Implicits.global

object AkkaServices extends AkkaActor {

  def getData(uri: String, settings : akka.http.scaladsl.settings.ConnectionPoolSettings, proxy : Boolean) = {
    /** Method to do a simple http get request*/

    if (proxy){
      val request = HttpRequest(HttpMethods.GET, uri)
      for {
        response <- Http().singleRequest(request, settings = settings)
        content <- Unmarshal(response.entity).to[String]
      } yield content

    } else {
      val request = HttpRequest(HttpMethods.GET, uri)
      for {
        response <- Http().singleRequest(request)
        content <- Unmarshal(response.entity).to[String]
      } yield content
    }
  }


  def request(sourceLink : String, settings : ConnectionPoolSettings, proxy : Boolean) : String = {
    val requestOutput = Await.result(getData(sourceLink, settings, proxy), 60.seconds)
    requestOutput
  }



  def getDataSession(loginUri : String ,uri: String, settings : akka.http.scaladsl.settings.ConnectionPoolSettings, proxy : Boolean) = {
    /** Method to do a http get request with cookies*/

      if (proxy){

        val login = Http(system).singleRequest(HttpRequest(HttpMethods.GET,loginUri), settings = settings)

        val resLogin = Await.result(login, 60 seconds)

        val sessionId = resLogin.headers.toList.filter(_.name.equals("Set-Cookie")).
          map { x => x.value.split(";")(0).split("=")(1)}.headOption

        val cookieHeader = akka.http.scaladsl.model.headers.Cookie("JSESSIONID",sessionId.get)

        val request = HttpRequest(HttpMethods.GET, uri, headers = List(cookieHeader))

        for {
          response <- Http().singleRequest(request, settings = settings)
          content <- Unmarshal(response.entity).to[String]
        } yield content

      } else {

        val login = Http(system).singleRequest(HttpRequest(HttpMethods.GET,loginUri))

        val resLogin = Await.result(login, 120 seconds)

        val sessionId = resLogin.headers.toList.filter(_.name.equals("Set-Cookie")).
          map { x => x.value.split(";")(0).split("=")(1)}.headOption

        val cookieHeader = akka.http.scaladsl.model.headers.Cookie("JSESSIONID",sessionId.get)

        val request = HttpRequest(HttpMethods.GET, uri, headers = List(cookieHeader))
        for {
          response <- Http().singleRequest(request)
          content <- Unmarshal(response.entity).to[String]
        } yield content
      }
  }

  def requestSession(loginUri : String ,sourceLink : String, settings : ConnectionPoolSettings, proxy : Boolean) : String = {
    val requestOutput = Await.result(getDataSession(loginUri,sourceLink, settings, proxy), 1200.seconds)
    requestOutput
  }


  def setUpProxy(proxyActive : String, hostProxy : String, portProxy : String): akka.http.scaladsl.settings.ConnectionPoolSettings = {
    /** Method to configure akka with proxy */

    if (proxyActive.toBoolean) {
      val httpsProxyTransport = ClientTransport.httpsProxy(InetSocketAddress.createUnresolved(hostProxy, portProxy.toInt))
      val settings = ConnectionPoolSettings(system)
        .withConnectionSettings(ClientConnectionSettings(system)
          .withTransport(httpsProxyTransport))
      settings

    } else {
      val settings = ConnectionPoolSettings(system)
        .withConnectionSettings(ClientConnectionSettings(system))
      settings

    }
  }
}
