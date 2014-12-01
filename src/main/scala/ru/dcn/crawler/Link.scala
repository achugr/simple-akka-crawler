package ru.dcn.crawler

import java.net.URL

/**
 * @author Artemii Chugreev artemij.chugreev@gmail.com
 *         Date: 30.11.14
 */
case class Link(link: URL, parentLink: Option[Link] = None, linkText: Option[String] = None, responseCode: Option[Int] = None)
