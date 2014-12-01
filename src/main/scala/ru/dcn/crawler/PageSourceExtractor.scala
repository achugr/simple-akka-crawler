package ru.dcn.crawler

import java.io.IOException
import java.net.{HttpURLConnection, URLConnection, URL}
import org.jsoup.Jsoup

import akka.actor.Actor.Receive
import akka.actor.{ActorLogging, Actor, ActorRef}
import org.jsoup.nodes.Document

/**
 * @author Artemii Chugreev artemij.chugreev@gmail.com
 *         Date: 30.11.14
 */
class PageSourceExtractor(linksExtractor: ActorRef, pingErrorsCollector: ActorRef)
  extends Actor with ActorLogging {

  case class HttpResponse(statusCode: Int, pageDoc: Document)

  def getPage(url: URL): Option[HttpResponse] = {
    val connection = url.openConnection()
    connection.connect()

    connection match {
      case httpConnection: HttpURLConnection =>
        val code = httpConnection.getResponseCode
        val is = connection.getInputStream
        try {
          Some(HttpResponse(code, Jsoup.parse(is, "UTF-8", url.toString)))
        }
        finally {
          is.close()
        }
      case _ =>
        None
    }
  }

  override def receive: Receive = {
    case Link(url, parentLink, text, responseCode) =>
      try {
        log.info(s"extracting page source of $url")

        getPage(url) match {
          case Some(HttpResponse(statusCode, pageTagNode)) =>
            statusCode match {
              case 200 =>
                val msg = new Page(new Link(url, parentLink, text, Some(statusCode)), pageTagNode)
                log.info("send link extractor msg")
                linksExtractor ! msg
              case badCode =>
                log.error(s"bad response code of $url")
                pingErrorsCollector ! new Link(url, parentLink, text, Some(badCode))
            }

          case None =>
            log.error(s"can't create http connection for $url")
            pingErrorsCollector ! new PingError(new Link(url, parentLink, text, Some(-1)), s"can't create http connection for $url")
        }
      }
  }
}
