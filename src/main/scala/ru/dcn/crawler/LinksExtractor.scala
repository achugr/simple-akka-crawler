package ru.dcn.crawler

import java.net.URL

import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Actor, ActorLogging}
import com.hazelcast.core.Hazelcast
import scala.collection.JavaConversions._

/**
 * @author Artemii Chugreev artemij.chugreev@gmail.com
 *         Date: 30.11.14
 */
class LinksExtractor extends Actor with ActorLogging {

  val hzInst = Hazelcast.getHazelcastInstanceByName("main")

  override def receive: Receive = {

    case Page(Link(link, parentLink, linkText, responseCode), doc) =>
      log.info(s"extracting links from page: ${link.toString}")

      for (el <- doc.select("a[href]")) {
        val href = el.attr("abs:href")

        if (!hzInst.getSet[String]("crawledLinks").contains(href)) {
          hzInst.getSet("crawledLinks").add(href)
          log.info(s"link extracted: $href")
          val msg = new Link(new URL(href), Some(new Link(link, parentLink, linkText, responseCode)), Some(el.text()), None)
          sender ! msg
        } else {
          log.info(s"link $href already crawled")
        }
      }
    case _ => log.error("pattern matching error")
  }
}
