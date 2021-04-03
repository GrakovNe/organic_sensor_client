package org.grakovne.organic.client.actors

import akka.actor.Actor
import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.Measurement

class MeasurementAggregator(configuration: OrganicSensorClientConfiguration) extends Actor {
  override def receive: Receive = {
    case Measurement(tvoc, carbonDioxide, temperature, humidity) =>
      println(s"$tvoc, $carbonDioxide, $temperature, $humidity")
    case _ => println("unwanted message")
  }
}


object MeasurementAggregator {
  def apply(configuration: OrganicSensorClientConfiguration) = new MeasurementAggregator(configuration)
}