package org.grakovne.organic.client.actors

import akka.actor.Actor
import org.grakovne.organic.client.common.MeasurementQueue
import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.Measurement

class MeasurementAggregator(configuration: OrganicSensorClientConfiguration) extends Actor {
  private val queue = new MeasurementQueue(configuration.averageThreshold * 2)
  private val counter = 0


  override def receive: Receive = {
    case measurement: Measurement =>
      println(queue.addMeasurementAndGetMeanValue(measurement))
    case _ => println("unwanted message")

    if (counter % configuration.averageThreshold == 0) {
      println(queue)
    }
  }
}

object MeasurementAggregator {
  def apply(configuration: OrganicSensorClientConfiguration) = new MeasurementAggregator(configuration)
}
