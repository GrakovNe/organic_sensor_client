package org.grakovne.organic.client.configuration

final case class MqttConfiguration(
  mqttHost: String,
  mqttTvocTopic: String,
  mqttDioxideTopic: String,
  mqttTemperatureTopic: String,
  mqttHumidityTopic: String)
