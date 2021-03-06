package edu.knoldus.utility

import java.text.SimpleDateFormat
import java.time.{Instant, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Date

import edu.knoldus.model.{BoundingBox, Coordinates, GPSData, Gyro, IMUData, ImageHeaderData, ImageObjects, LinAcc, Magnetometer, ObjectItem, Quaternion}
import sun.util.calendar.ZoneInfo

object DataGenerator {
  val DATE_FORMAT = "dd-MM-yy HH:mm:ss SSS"
  val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.of("UTC"))

  case class PublisherModel(imageHeaderData: List[ImageHeaderData],
                            gpsData: List[GPSData],
                            imuData: List[IMUData],
                            imageObjects: List[ImageObjects])
  val getGpsTime: (Long, Int) => Long = (time: Long, count: Int) => if(count % 2 == 0) time + count else time - count
  private def getImageHeaderData: List[ImageHeaderData] = {
    val unitId = java.util.UUID.randomUUID().toString
    (1 to 6000).map(count => {
      val imageId = java.util.UUID.randomUUID().toString
      val cameraId = "ASD$1231241"
      Thread.sleep(100)
      ImageHeaderData(s"$imageId-$count",
        unitId,
        cameraId,
        "ipAddress",
        System.currentTimeMillis(),
        0.0f,
        false,
        1,
        2,
        3,
        4,
        6,
        5,
        9,
        None,
        count)
    }).toList
  }

  def getDataToPublish: PublisherModel = {
    val imageHeaderDataList = getImageHeaderData
    val gpsDataImuDataList: List[(GPSData, IMUData)] = imageHeaderDataList.flatMap(imageHeaderData => {
      (1 to 6000).map(count => {
        (GPSData(
          "gpsId",
          None,
          getGpsTime(imageHeaderData.timestamp,count),
          FileUtility.GPS_DATE_FORMATTER.format(Instant.now()),
          Coordinates(56, 36.9658),
          "N",
          Coordinates(56, 36.9658),
          "W",
          5.6,
          36.96,
          count % 2 == 0,
          None,
          "None"
          ),
         IMUData("imuId",
          getGpsTime(imageHeaderData.timestamp, count + 1),
           getGpsTime(imageHeaderData.timestamp, count + 1),
           None,
           LinAcc(1,2,3),
           Magnetometer(7,8,9),
           Gyro(4,5,6),
           Quaternion(9, 6, 3, 8),
           "None",
           None
         ))
      }).toList
    })

    val (gpsList, imuList): (List[GPSData], List[IMUData]) = gpsDataImuDataList
      .foldLeft(List.empty[GPSData], List.empty[IMUData])((splitedData, bothData) => (splitedData._1 ::: List(bothData._1), splitedData._2 ::: List(bothData._2)))


    val imageObjects: List[ImageObjects] = imageHeaderDataList.zipWithIndex.map{
      case (headerData, index) =>
        val objectDetectorId = java.util.UUID.randomUUID.toString
        ImageObjects(
          headerData.imageId,
          objectDetectorId,
          (1 to 5).map(value => {
            ObjectItem(
              (value * index + 1) ,
              headerData.imageId,
              value,
              5.6,
              value,
              index,
              BoundingBox(index, value,index, value),
              "VfCqD8wz",
              Some("map")
            )
          }).toList,
          formatter.format(Instant.now)
        )
    }

    PublisherModel(imageHeaderDataList, gpsList, imuList, imageObjects)
  }

  val rnd = new scala.util.Random
  def getRandomInt(start: Int, end: Int): Int = start + rnd.nextInt( (end - start) + 1 )
//  def getRandomFloat(start: Int, end: Int): Int = start + rnd.nextFloat((end - start) + 1 )
}
