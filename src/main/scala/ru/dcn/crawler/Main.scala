package ru.dcn.crawler

import java.net.URL

import akka.actor.{Props, ActorSystem}
import akka.routing.FromConfig
import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast

/**
 * @author Artemii Chugreev artemij.chugreev@gmail.com
 *         Date: 30.11.14
 */
object Main extends App {

  val hzInst = Hazelcast.newHazelcastInstance(new Config("main"))

  val baseUrl: URL = new URL("http://help.yandex.ru/")

  val system = ActorSystem("crawler")

  val linksExtractor = system.actorOf(
    Props[LinksExtractor].
      withRouter(FromConfig()), "links-extractor")

  val pingErrorsCollector = system.actorOf(
    Props[PingErrorsCollector].
      withRouter(FromConfig()), "pings-error-collector")

  val pageSourceExtractor = system.actorOf(
    Props(new PageSourceExtractor(linksExtractor, pingErrorsCollector)).
      withRouter(FromConfig()), "page-source-extractor")


  pageSourceExtractor ! Link(baseUrl)

}
