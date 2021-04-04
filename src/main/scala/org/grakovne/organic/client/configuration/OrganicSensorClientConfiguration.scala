package org.grakovne.organic.client.configuration

case class OrganicSensorClientConfiguration(
  serialPortName: String,
  averageThreshold: Int,
  tvocKey: String,
  dioxideKey: String,
  temperatureKey: String,
  humidityKey: String,
  measurementsDelimiter: String,
  measurementSeparator: String,
  mqttHost: String,
  mqttTvocTopic: String,
  mqttDioxideTopic: String,
  mqttTemperatureTopic: String,
  mqttHumidityTopic: String
)
