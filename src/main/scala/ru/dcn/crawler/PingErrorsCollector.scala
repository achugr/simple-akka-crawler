package ru.dcn.crawler

import akka.actor.{ActorLogging, Actor, ActorRef}

/**
 * @author Artemii Chugreev artemij.chugreev@gmail.com
 *         Date: 30.11.14
 */
class PingErrorsCollector extends Actor with ActorLogging {
  override def receive: Receive = {
    case PingError(link, message) =>
      log.error(s"link: ${link.link.toString}, message: $message")
  }
}
