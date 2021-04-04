package org.grakovne.organic.client.common

import org.grakovne.organic.client.messages.Measurement

import scala.collection.mutable
import scala.math.BigDecimal.RoundingMode

class MeasurementQueue(limit: Int) extends mutable.Queue[Measurement] {

  def addMeasurementAndGetMeanValue(element: Measurement): Measurement = {
    super.enqueue(element)

    if (super.size > limit) {
      super.dequeue()
    }

    meanValue
  }

  private def meanValue(extractor: (Measurement => Double)): Double = {
    val rawMeanValue = this.map(extractor).sum / (if (this.isEmpty) 1 else this.size)
    BigDecimal(rawMeanValue).setScale(2, RoundingMode.HALF_UP).doubleValue
  }

  private def meanValue: Measurement = Measurement(
    tvoc = meanValue(_.tvoc),
    carbonDioxide = meanValue(_.carbonDioxide),
    temperature = meanValue(_.temperature),
    humidity = meanValue(_.humidity)
  )
}

object MeasurementQueue {
  def apply(limit: Int): MeasurementQueue = new MeasurementQueue(limit)
}
