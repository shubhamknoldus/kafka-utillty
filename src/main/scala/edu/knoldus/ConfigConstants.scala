package edu.knoldus

object ConfigConstants {
  val ecThreads = 50
  val imageHeaderTopic: String = ConfigProvider.getEnvString("kafka-config.topics.image-header")
  val imageTopic: String = ConfigProvider.getEnvString("kafka-config.topics.image-message")
  val imageHeaderTopicProcessed: String = ConfigProvider.getEnvString("kafka-config.topics.image-header-processed")
  val imageIMUTopicPublish: String = ConfigProvider.getEnvString("kafka-config.topics.image-imu-publish")
  val imageIMUTopicSubscribe: String = ConfigProvider.getEnvString("kafka-config.topics.image-imu-subscribe")
  val imageGPSTopicPublish: String = ConfigProvider.getEnvString("kafka-config.topics.image-gps-publish")
  val imageGPSTopicSubscribe: String = ConfigProvider.getEnvString("kafka-config.topics.image-gps-subscribe")
  val imageGPSData: String = ConfigProvider.getEnvString("kafka-config.topics.image-gps-data")
  val imageIMUData: String = ConfigProvider.getEnvString("kafka-config.topics.image-imu-data")
  val imageObjects: String = ConfigProvider.getEnvString("kafka-config.topics.image-objects")
  val trackingData: String = ConfigProvider.getEnvString("kafka-config.topics.tracking-data")
  val stereoData: String = ConfigProvider.getEnvString("kafka-config.topics.stereo-data")
  val kafkaBootStrapServer: String = ConfigProvider.getEnvString("kafka-config.bootstrap-server")
  val imagesPerCamera: Int = ConfigProvider.getEnvInt("kafka-config.images-per-camera") / 10
  val numCameras: Int = ConfigProvider.getEnvInt("kafka-config.num-cameras")
  if(numCameras > ecThreads - 9) {
    println(s"Num camera cant be more than 40 as the machine only using $ecThreads have only ${sys.runtime.availableProcessors()}  cores ")
    sys.exit()
  }
}
