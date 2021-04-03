package org.grakovne.organic.client.actors

import akka.actor.{Actor, ActorRef}
import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.{Measurement, ReadMeasurement}
import org.grakovne.organic.client.services.{MeasurementParser, SerialPortReader}

class MeasurementReader(aggregator: ActorRef, configuration: OrganicSensorClientConfiguration) extends Actor {

  private val serialPortReader: SerialPortReader = SerialPortReader(configuration.serialPortName)
  private val measurementParser = MeasurementParser(configuration)

  override def receive: Receive = {
    case ReadMeasurement => measurementParser.parse(serialPortReader.readLine()).map(aggregator ! _)
    case _ => println("unwanted message")
  }
}

object MeasurementReader {
  def apply(aggregator: ActorRef, configuration: OrganicSensorClientConfiguration) =
    new MeasurementReader(aggregator, configuration)
}
