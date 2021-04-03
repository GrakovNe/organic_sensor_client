package org.grakovne.organic.client


import akka.actor._
import org.grakovne.organic.client.actors.{MeasurementAggregator, MeasurementReader}
import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.ReadMeasurement
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps

object OrganicSensorApplication extends App {
  val configuration = ConfigSource.default.loadOrThrow[OrganicSensorClientConfiguration]
  val system = ActorSystem("OrganicSensorSystem")

  val aggregator = system.actorOf(Props(MeasurementAggregator(configuration)), name = "MeasurementAggregator")
  val reader = system.actorOf(Props(MeasurementReader(aggregator, configuration)), name = "MeasurementReader")

  system.scheduler.scheduleWithFixedDelay(Duration.Zero, 1 second, reader, ReadMeasurement)
}
