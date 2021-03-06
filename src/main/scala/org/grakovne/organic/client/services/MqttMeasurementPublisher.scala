package org.grakovne.organic.client.services

import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttException.REASON_CODE_CLIENT_CONNECTED
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.grakovne.organic.client.common.InfinitiveRetry
import org.grakovne.organic.client.configuration.OrganicSensorClientConfiguration
import org.grakovne.organic.client.messages.Measurement
import org.grakovne.organic.client.services.MqttMeasurementPublisher.buildConnection
import org.grakovne.organic.client.services.MqttMeasurementPublisher.buildConnectionOptions
import org.grakovne.organic.client.services.MqttMeasurementPublisher.isConnectionFailed
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import scala.util.Failure
import scala.util.Success
import scala.util.Try

class MqttMeasurementPublisher(configuration: OrganicSensorClientConfiguration) {

  private val mqttClient = buildConnection(configuration.mqttConfiguration.mqttHost)
  private val log: Logger = LoggerFactory.getLogger(this.getClass)

  def publishMeasurement(measurement: Measurement): Unit = {
    openConnection()

    publish(measurement.tvoc, configuration.mqttConfiguration.mqttTvocTopic)
    publish(measurement.carbonDioxide, configuration.mqttConfiguration.mqttDioxideTopic)
    publish(measurement.temperature, configuration.mqttConfiguration.mqttTemperatureTopic)
    publish(measurement.humidity, configuration.mqttConfiguration.mqttHumidityTopic)
  }

  private def publish[T](payload: T, topic: String): Unit = {
    try {
      mqttClient.publish(topic, new MqttMessage(payload.toString.getBytes()))
    } catch {
      case exception: Exception => log.error(s"Unable to send message to MQTT topic due: $exception")
    }
  }

  private def connectMqtt(): Boolean = Try(mqttClient.connect(buildConnectionOptions)) match {
    case Success(_) => true
    case Failure(exception) => !isConnectionFailed(exception)
  }

  private def openConnection(): MqttAsyncClient = InfinitiveRetry().retry(
    () => {
      connectMqtt() match {
        case true => Option(mqttClient)
        case false => Option.empty
      }
    },
    Option(s"Unable to connect to MQTT server, retrying...")
  )
}

object MqttMeasurementPublisher {
  def apply(configuration: OrganicSensorClientConfiguration): MqttMeasurementPublisher = new MqttMeasurementPublisher(
    configuration)

  def buildConnection(host: String): MqttAsyncClient =
    new MqttAsyncClient(host, MqttAsyncClient.generateClientId, new MemoryPersistence)

  private def isConnectionFailed(exception: Throwable) = exception match {
    case ex: MqttException => isMqttConnectionFailed(ex)
    case _: Exception => true
  }

  private def isMqttConnectionFailed(ex: MqttException): Boolean = ex.getReasonCode match {
    case REASON_CODE_CLIENT_CONNECTED => false
    case _ => true
  }

  private def buildConnectionOptions: MqttConnectOptions = {
    val options = new MqttConnectOptions()
    options.setAutomaticReconnect(true)
    options
  }
}
