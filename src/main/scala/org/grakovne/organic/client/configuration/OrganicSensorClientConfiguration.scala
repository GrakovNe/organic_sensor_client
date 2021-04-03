package org.grakovne.organic.client.configuration

case class OrganicSensorClientConfiguration(serialPortName: String,
                                            averageThreshold: String,
                                            tvocKey: String,
                                            dioxideKey: String,
                                            temperatureKey: String,
                                            humidityKey: String,
                                            measurementsDelimiter: String,
                                            measurementSeparator: String)
