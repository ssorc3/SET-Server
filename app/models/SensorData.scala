package models

case class TemperatureData(id: Int, deviceID: String, timestamp: Long, value: Double)
case class HumidityData(id: Int, deviceID: String, timestamp: Long, value: Double)
case class LightData(id: Int, deviceID: String, timestamp: Long, value: Double)