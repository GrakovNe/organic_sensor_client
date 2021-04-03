package org.grakovne.organic.client.messages

final case class Measurement(tvoc: Int, carbonDioxide: Int, temperature: Double, humidity: Double)

class MeasurementBuilder {
  private var tvoc: Option[Int] = Option.empty[Int]
  private var carbonDioxide: Option[Int]  = Option.empty[Int]
  private var temperature: Option[Double]  = Option.empty[Double]
  private var humidity: Option[Double]  = Option.empty[Double]


  def insertTvoc(tvoc: Option[Int]): MeasurementBuilder = {
    this.tvoc = tvoc
    this
  }

  def insertCarbonDioxide(carbonDioxide: Option[Int]): MeasurementBuilder = {
    this.carbonDioxide = carbonDioxide
    this
  }

  def insertTemperature(temperature: Option[Double]): MeasurementBuilder = {
    this.temperature = temperature
    this
  }

  def insertHumidity(humidity: Option[Double]): MeasurementBuilder = {
    this.humidity = humidity
    this
  }

  def build(): Option[Measurement] = for {
    tvoc <- tvoc
    carbonDioxide <- carbonDioxide
    temperature <- temperature
    humidity <- humidity
  } yield Measurement(tvoc, carbonDioxide, temperature, humidity)
}