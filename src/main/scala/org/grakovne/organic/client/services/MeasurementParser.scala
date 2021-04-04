package org.grakovne.organic.client.services

import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.Measurement
import org.grakovne.organic.client.messages.MeasurementBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MeasurementParser(configuration: OrganicSensorClientConfiguration) {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  private def parseMeasurement(rawMeasurement: String, builder: MeasurementBuilder): Unit = {
    val data = rawMeasurement.split(configuration.measurementSeparator).toList

    val name = data.headOption.map(_.trim)
    val value = data.lastOption.map(_.trim)

    name.map {
      case configuration.measurementKeys.tvocKey => builder.insertTvoc(value.flatMap(_.toDoubleOption))
      case configuration.measurementKeys.dioxideKey => builder.insertCarbonDioxide(value.flatMap(_.toDoubleOption))
      case configuration.measurementKeys.temperatureKey => builder.insertTemperature(value.flatMap(_.toDoubleOption))
      case configuration.measurementKeys.humidityKey => builder.insertHumidity(value.flatMap(_.toDoubleOption))
      case unwanted => log.warn(s"Unable to parse string: $unwanted as measurement result")
    }

  }

  def parse(raw: String): Option[Measurement] = {
    val measurementBuilder = new MeasurementBuilder()

    raw
      .split("\n")(0)
      .trim
      .split(configuration.measurementsDelimiter)
      .foreach(parseMeasurement(_, measurementBuilder))

    measurementBuilder.build()
  }
}

object MeasurementParser {
  def apply(configuration: OrganicSensorClientConfiguration): MeasurementParser = new MeasurementParser(configuration)
}
