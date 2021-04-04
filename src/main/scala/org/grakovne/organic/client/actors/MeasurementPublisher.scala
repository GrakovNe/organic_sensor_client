package org.grakovne.organic.client.actors

import akka.actor.Actor
import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.Measurement
import org.grakovne.organic.client.services.MqttMeasurementPublisher

class MeasurementPublisher(configuration: OrganicSensorClientConfiguration) extends Actor {
  private val mqttMeasurementPublisher = MqttMeasurementPublisher(configuration)
  override def receive: Receive = {
    case measurement: Measurement => mqttMeasurementPublisher.publishMeasurement(measurement)
    case _ => println("unwanted message")
  }
}

object MeasurementPublisher {
  def apply(configuration: OrganicSensorClientConfiguration) = new MeasurementPublisher(configuration)
}
