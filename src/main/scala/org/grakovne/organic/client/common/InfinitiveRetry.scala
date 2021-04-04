package org.grakovne.organic.client.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.util.Failure
import scala.util.Success
import scala.util.Try

class InfinitiveRetry {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val secondInMills = 1000

  def retry[T](
    action: () => Option[T],
    failedMessage: Option[String] = Option.empty,
    timeoutMs: Int = secondInMills): T = Try(action.apply()) match {
    case Success(Some(value)) => value
    case Success(None) => onFailed(action, failedMessage, timeoutMs)
    case Failure(ex) => onFailed(action, failedMessage.map(str => s"$str caught exception: $ex"), timeoutMs)
  }

  private def onFailed[T](action: () => Option[T], failedMessage: Option[String], timeoutMs: Int): T = {
    failedMessage.foreach(log.warn)
    Thread.sleep(timeoutMs)
    retry(action, failedMessage, timeoutMs)
  }
}

object InfinitiveRetry {
  def apply(): InfinitiveRetry = new InfinitiveRetry()
}
