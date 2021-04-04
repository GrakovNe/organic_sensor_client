package org.grakovne.organic.client.services

import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttException._
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.Measurement
import org.grakovne.organic.client.services.MqttMeasurementPublisher.buildConnection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MqttMeasurementPublisher(configuration: OrganicSensorClientConfiguration) {
  private val mqttClient = buildConnection(configuration.mqttHost)

  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  def publishMeasurement(measurement: Measurement): Unit = {
    while (!mqttClient.isConnected) {
      connectMqtt()
    }

    publishTextMessage(measurement.tvoc, configuration.mqttTvocTopic)
    publishTextMessage(measurement.carbonDioxide, configuration.mqttDioxideTopic)
    publishTextMessage(measurement.temperature, configuration.mqttTemperatureTopic)
    publishTextMessage(measurement.humidity, configuration.mqttHumidityTopic)
  }

  def publishTextMessage[T](messageText: T, topic: String): Unit = {
    try {
      mqttClient.publish(topic, new MqttMessage(messageText.toString.getBytes()))
    } catch {
      case exception: Exception => log.error(s"Unable to send message to MQTT topic due: $exception")
    }
  }

  private def connectMqtt(): Unit =
    try {
      mqttClient.connect(buildConnectionOptions)
    } catch {
      case exception: MqttException =>
        if (isConnectionFailed(exception)) {
          reconnectOnException(exception)
        }
      case exception: Exception =>
        reconnectOnException(exception)
    }

  private def isConnectionFailed(exception: MqttException) = exception.getReasonCode match {
    case REASON_CODE_CLIENT_CONNECTED | REASON_CODE_CONNECT_IN_PROGRESS => false
    case _ => true
  }

  private def buildConnectionOptions = {
    val options = new MqttConnectOptions()
    options.setAutomaticReconnect(true)
    options
  }

  private def reconnectOnException(exception: Exception): Unit = {
    log.error(s"Unable to connect to MQTT server due: $exception, retrying in 1 second")
    Thread.sleep(1000)
    connectMqtt()
  }
}

object MqttMeasurementPublisher {
  def apply(configuration: OrganicSensorClientConfiguration): MqttMeasurementPublisher = new MqttMeasurementPublisher(
    configuration)

  def buildConnection(host: String): MqttAsyncClient =
    new MqttAsyncClient(host, MqttAsyncClient.generateClientId, new MemoryPersistence)
}
