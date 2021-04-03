package org.grakovne.organic.client.services

import com.fazecast.jSerialComm.SerialPort
import org.slf4j.{Logger, LoggerFactory}

class SerialPortReader(name: String) {

  private val second_in_mills = 1000
  val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val port = requirePort(name)

  def readLine(): String = {
    while (!port.isOpen) {
      requirePort(name)
    }

    blockingWaitForData()

    val readBuffer = new Array[Byte](port.bytesAvailable)

    port.readBytes(readBuffer, readBuffer.length)
    readBuffer.map(f => f.toChar).mkString
  }

  private def blockingWaitForData(): Unit = {
    while (port.bytesAvailable() == 0) {
      Thread.sleep(second_in_mills)
    }
  }

  private def requirePort(name: String): SerialPort = {
    val port = SerialPort
      .getCommPorts
      .filter(_.getDescriptivePortName == name)
      .toList match {
      case head :: Nil => head
      case Nil | _ :: _ :: _ =>
        log.error(s"Unable to find the only one port with name $name")
        Thread.sleep(second_in_mills)
        requirePort(name)
    }

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
