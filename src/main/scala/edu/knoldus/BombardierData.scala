package edu.knoldus

import java.time.Instant

import edu.knoldus.model._
import edu.knoldus.producer.DataProducer
import edu.knoldus.utility.FileUtility
import net.liftweb.json.DefaultFormats
import net.liftweb.json.Serialization.write

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object DummyData{

  val rnd = new scala.util.Random
  def getRandomInt(start: Int, end: Int): Int = start + rnd.nextInt( (end - start) + 1 )

  val imageHeader = ImageHeaderData(s"",
    "",
    "",
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
    0)

  val gpsData = GPSData(
    "gpsId",
    None,
    0,
    FileUtility.GPS_DATE_FORMATTER.format(Instant.now()),
    Coordinates(56, 36.9658),
    "N",
    Coordinates(56, 36.9658),
    "W",
    5.6,
    36.96,
    false,
    None,
    "None"
  )
  val imuData = IMUData("imuId",
    0,
    0,
    None,
    LinAcc(1,2,3),
    Magnetometer(7,8,9),
    Gyro(4,5,6),
    Quaternion(9, 6, 3, 8),
    "None",
    None
  )
}

object BombardierData extends App {
  implicit val formats: DefaultFormats.type = DefaultFormats
  val lambda = (x: Int, y: Int) => (x + y, x - y, x * y, x / y)
  val uniqueObjects = List("Person","Car", "Building", "Bus", "Pole", "Bag", "Drone", "Truck", "Person", "Person")
//  println(s"========================= ${FutureHelper.fileList}")
// val imagesPerCamera: List[File] = FutureHelper.fileList.zipWithIndex.flatMap{
//   case (element, index)=>
//     println(s"============== statring for index $index")
//     (1 to 180).map(_ => element)
// }
   val imagesPerCamera: List[Int] = (1 to 10).toList.zipWithIndex.flatMap{
     case (element, index)=>
       println(s"============== statring for index $index")
       (1 to 2).map(_ => element)
   }
  val cameraId = "ASD$1231241"
//  val cameraIds = (1 to 10).map(count => s"$cameraId-$count")
//   val unitIds = (1 to 20).toList.map(_ => "77e5afa2-d882-11e9-994a-00044be64e82")
  val unitIds = List("0df6d895-2ae8-498a-adc1-5fc96cbd583e")
// val unitIds = List(java.util.UUID.randomUUID.toString)
// val unitIds = (1 to 10).toList.map(_ => java.util.UUID.randomUUID.toString)
  val imageHeaderData = DummyData.imageHeader
  val gpsData = DummyData.gpsData
  val imuData = DummyData.imuData
  val maxCounter = imagesPerCamera.length - 1

  def publishImageHeader: Future[List[(ObjectDataMessage, String)]] = Future {
    val objectDetector = java.util.UUID.randomUUID.toString
    unitIds.flatMap (unitId => {
      println(s"publishing for $unitId")
      val imageId = java.util.UUID.randomUUID().toString
      imagesPerCamera.zipWithIndex.map { case (file, index: Int) =>
//           val byteArray = Files.readAllBytes(file.toPath) //excess overhead
        DataProducer.writeToKafka(ConfigConstants.imageHeaderTopic, unitId, write(imageHeaderData.copy(timestamp = System.currentTimeMillis(), imageId = f"$imageId-$index%05d", unitId = unitId, imageCounter = index)))
//         DataProducer.writeImageToKafka(ConfigConstants.imageTopic, s"$unitId-$imageId-L",f"${unitId}_$imageId-$index%05d-L.jpg", byteArray, maxCounter)
//         DataProducer.writeImageToKafka(ConfigConstants.imageTopic, s"$unitId-$imageId-R",f"${unitId}_$imageId-$index%05d-R.jpg", byteArray, maxCounter)
        DataProducer.writeImageToKafka(ConfigConstants.imageTopic, s"$unitId-$imageId-R",f"${unitId}_$imageId-$index%05d-R.jpg", WebCamTester.getImage, maxCounter)
        DataProducer.writeImageToKafka(ConfigConstants.imageTopic, s"$unitId-$imageId-L",f"${unitId}_$imageId-$index%05d-L.jpg", WebCamTester.getImage, maxCounter)
        Thread.sleep(100)
        publishImageObjects(unitId, f"$imageId-$index%05d", imageId, objectDetector, index)
      }
    })
  }

  def publishGPSData = Future {
    println(unitIds.length)
    unitIds foreach { camera =>
      imagesPerCamera.foreach { _ =>
        DataProducer.writeToKafka(ConfigConstants.imageGPSTopicSubscribe, camera,
          write(gpsData.copy(timestampLinux = System.currentTimeMillis(),
         latitude = Coordinates((System.currentTimeMillis() % 360).toInt, System.currentTimeMillis() % 60), latitudeNS = if(System.currentTimeMillis() % 2 == 0) "N" else "S",
         longitude = Coordinates((System.currentTimeMillis() % 360).toInt, System.currentTimeMillis() % 60), longitudeEW = if(System.currentTimeMillis() % 2 == 0) "E" else "W",
          unitId = camera,
          timestampGPS = FileUtility.GPS_DATE_FORMATTER.format(Instant.now()))))
        Thread.sleep(100)
      }
    }
  }


  def publishIMUData = Future {
    println(unitIds.length)

    unitIds foreach { camera =>
      imagesPerCamera.foreach { _ =>
        (1 to 10) foreach { _ =>
          DataProducer.writeToKafka(ConfigConstants.imageIMUTopicSubscribe, camera, write(imuData.copy(unitId = camera, timeStampLinux = System.currentTimeMillis())))
          Thread.sleep(10)
        }
      }
    }
  }

  def publishImageObjects(unitId: String, imageId: String, imageUUID: String, objectDetector: String, counter: Int): (ObjectDataMessage, String) = {
    val data = if(counter % 5 == 0){//every fifth image would be empty
      ObjectDataMessage(
        ImageMessage(Array.empty[Int], true, imageId,imageUUID, s"$imageId.jpg" , s"$imageId.jpg", unitId),
        None,
        "yolo3", Instant.now().toEpochMilli
      )
    } else{
      ObjectDataMessage(
        ImageMessage(Array.empty[Int], false, imageId,imageUUID, s"$imageId.jpg" , s"$imageId.jpg", unitId),
        Some((0 to (counter % 10)).toList.map(value => ObjectData(counter+value, value, uniqueObjects(value), 3.45, BoundingBox(1,2,3,4), 4.5 * value))),
        "yolo3", Instant.now().toEpochMilli
      )
    }
    DataProducer.writeToKafka(ConfigConstants.imageObjects, imageId, write(data))
    (data, imageId)
  }

  def publishTrackingData(trackingData: List[TrackingData]) = {
    trackingData.foreach(trackData => {
      DataProducer.writeToKafka(ConfigConstants.trackingData, trackData.unitId, write(trackData))
    })
  }

  def generateTrackingData(objects: List[(ObjectDataMessage, String)]): Future[List[TrackingData]] = Future {
    (objects.zipWithIndex flatMap  {case ((imgObject, imageId), index) =>
      if(imgObject.ImageData.imageEmpty){
        None
      } else{
        Some(imgObject.imageObjects.get.map(imageObjectData => {
          val occ = (1 to new java.util.Random().nextInt(10)).toList
          TrackingData(imgObject.ImageData.unitId,
            s"${imgObject.ImageData.imageUUID}-T-${imageObjectData.objId}",
            imageObjectData.objLabelDefinition,imgObject.ImageData.imageUUID,
            if(index % 2 == 0) 0.6 else 0.3,
            (if(occ.isEmpty) List(1) else occ) map (index2 => {
              Occurrence(s"$imageId",
                imgObject.timestamp,
                BoundingBox(4, DummyData.getRandomInt(0, 360), 5, 9),
                if(index % 2 == 0) 0.6 else 0.3, 12)
            }))
        }))
      }
    }).flatten
  }

  def publishAll: Future[Unit] = for{
    objectList <- publishImageHeader
    imgObject <- generateTrackingData(objectList)
  } yield publishTrackingData(imgObject)

  val res = Future.sequence(List(publishGPSData, publishIMUData, publishAll))

  Await.ready(res.map(_ => {
    println("================================ Process completed")
    WebCamTester.closeCam
    0
  }), Duration.Inf)
}
