package org.grakovne.organic.client.configuration

case class OrganicSensorClientConfiguration(
  serialPortName: String,
  averageThreshold: Int,
  measurementsDelimiter: String,
  measurementSeparator: String,
  measurementKeys: MeasurementKeyConfiguration,
  mqttConfiguration: MqttConfiguration
)
