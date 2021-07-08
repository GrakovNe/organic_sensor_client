package org.grakovne.organic.client.services

import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.Measurement
import org.grakovne.organic.client.messages.MeasurementBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MeasurementParser(configuration: OrganicSensorClientConfiguration) {

  private val log: Logger = LoggerFactory.getLogger(this.getClass)
  private var debounceCounter = 0

  private def parseMeasurement(rawMeasurement: String, builder: MeasurementBuilder): Unit = {
    val data = rawMeasurement.split(configuration.measurementSeparator).toList

    val name = data.headOption.map(_.trim)
    val value = data.lastOption.map(_.trim)

    log.info((name, value).toString())

    name.map {
      case configuration.measurementKeys.tvocKey => builder.insertTvoc(value.flatMap(_.toDoubleOption))
      case configuration.measurementKeys.dioxideKey => builder.insertCarbonDioxide(value.flatMap(_.toDoubleOption))
      case configuration.measurementKeys.temperatureKey => builder.insertTemperature(value.flatMap(_.toDoubleOption))
      case configuration.measurementKeys.humidityKey => builder.insertHumidity(value.flatMap(_.toDoubleOption))
      case unwanted => log.warn(s"Unable to parse string: $unwanted as measurement result")
    }

  }

  def parse(raw: String): Option[Measurement] = {

    isDataValid(raw) match {
      case true => reduceFromDebounce()
      case false => raiseToDebounce()
    }

    isDebounceRaised() match {
      case true => None
      case false => parseData(raw)
    }
  }

  private def parseData(raw: String): Option[Measurement] = {
    val measurementBuilder = new MeasurementBuilder()

    raw
      .split("\n")(0)
      .trim
      .split(configuration.measurementsDelimiter)
      .foreach(parseMeasurement(_, measurementBuilder))

    log.debug(s"got measurement: ${measurementBuilder.build()}")
    measurementBuilder.build()
  }

  private def raiseToDebounce(): Unit = debounceCounter = 12

  private def reduceFromDebounce(): Unit =
    if (debounceCounter < 1) debounceCounter = 0 else debounceCounter = debounceCounter - 1

  private def isDebounceRaised(): Boolean = debounceCounter != 0

  private def isDataValid(raw: String) = !raw.toLowerCase().contains("nan")
}

object MeasurementParser {
  def apply(configuration: OrganicSensorClientConfiguration): MeasurementParser = new MeasurementParser(configuration)
}
