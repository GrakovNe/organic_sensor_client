package org.grakovne.organic.client.actors

import akka.actor.Actor
import akka.actor.ActorRef
import org.grakovne.organic.client.common.MeasurementQueue
import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.Measurement

class MeasurementAggregator(publisher: ActorRef, configuration: OrganicSensorClientConfiguration) extends Actor {
  private val queue = new MeasurementQueue(configuration.averageThreshold)

  override def receive: Receive = {
    case measurement: Measurement => publisher ! queue.addMeasurementAndGetMeanValue(measurement)
    case _ => println("unwanted message")
  }
}

object MeasurementAggregator {
  def apply(publisher: ActorRef, configuration: OrganicSensorClientConfiguration) =
    new MeasurementAggregator(publisher: ActorRef, configuration)
}
