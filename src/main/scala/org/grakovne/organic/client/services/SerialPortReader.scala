package org.grakovne.organic.client.services

import com.fazecast.jSerialComm.SerialPort
import org.grakovne.organic.client.common.InfinitiveRetry
import org.slf4j.{Logger, LoggerFactory}

import scala.annotation.tailrec

class SerialPortReader(name: String) {

  val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val port = requirePort(name)

  def readLine(): String = {
    while (!port.isOpen) {
      requirePort(name)
    }

    blockingWaitForData()

    val readBuffer = new Array[Byte](port.bytesAvailable)

    port.readBytes(readBuffer, readBuffer.length)
    readBuffer.map(_.toChar).mkString
  }

  private def blockingWaitForData(): Unit = {
    InfinitiveRetry().retry(() => {
      port.bytesAvailable() match {
        case 0 => None
        case other => Some(other)
      }
    })
  }

  @tailrec
  private def requirePort(name: String): SerialPort = {

    val port: SerialPort = InfinitiveRetry().retry(
      () => {
        SerialPort
          .getCommPorts
          .toList
          .find(_.getDescriptivePortName == name) match {
          case Some(value) => Option(value)
          case None =>
            log.error(s"Unable to open port $name. Available ports is\n: ${SerialPort.getCommPorts.mkString("\n")}")
            Option.empty
        }
      },
      Option(s"Unable to find the only one port with name $name")
    )

    port.openPort()
    port.isOpen match {
      case true => port
      case false => requirePort(name)
    }
  }
}

object SerialPortReader {
  def apply(name: String): SerialPortReader = new SerialPortReader(name)
}
