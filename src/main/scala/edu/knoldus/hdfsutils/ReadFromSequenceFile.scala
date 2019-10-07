package edu.knoldus.hdfsutils

import edu.knoldus.model.ImageAggregated

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global



object ReadFromSequenceFile extends App {
  val list = List("0c609de0-e3b4-11e9-b847-00044be64e82","12d13cc2-e3c6-11e9-b0e0-00044be64e82","12f9de22-e3b5-11e9-b632-00044be64e82","14da0f5e-e3bb-11e9-9d79-00044be64e82","17335656-e3c1-11e9-8df9-00044be64e82","17711f4c-e3b0-11e9-a8e5-00044be64e82","19955b5c-e3b6-11e9-adf4-00044be64e82","1b5a18b4-e3bc-11e9-b045-00044be64e82","1e13781c-e3b1-11e9-978e-00044be64e82","21f8babc-e3bd-11e9-bbf9-00044be64e82","24524b9c-e3c3-11e9-9842-00044be64e82","24a44110-e3b2-11e9-a2be-00044be64e82","26cead8a-e3b8-11e9-bb9e-00044be64e82","2d5c6cae-e3b9-11e9-b140-00044be64e82","31dd7520-e3b4-11e9-8573-00044be64e82","33daace8-e3ba-11e9-8855-00044be64e82","360090e0-e3c0-11e9-af47-00044be64e82","3cbfb05e-e3c1-11e9-99cb-00044be64e82","3cefc1ce-e3b0-11e9-adc2-00044be64e82","3f136f68-e3b6-11e9-9ffb-00044be64e82","40d97fc6-e3bc-11e9-a2e7-00044be64e82","435e0a04-e3c2-11e9-8e5e-00044be64e82","439f5b78-e3b1-11e9-a6a0-00044be64e82","4a31588c-e3b2-11e9-b4ce-00044be64e82","4c4cc6f0-e3b8-11e9-99a9-00044be64e82","50e2dd8a-e3b3-11e9-9b15-00044be64e82","52d8ab6e-e3b9-11e9-b524-00044be64e82","575b7b80-e3b4-11e9-8ba5-00044be64e82","5958a830-e3ba-11e9-a29d-00044be64e82","5bda5924-e3af-11e9-9a4a-00044be64e82","5dea60d0-e3c6-11e9-893a-00044be64e82","624ea15e-e3c1-11e9-860a-00044be64e82","626e9902-e3b0-11e9-aaed-00044be64e82","665b2f06-e3bc-11e9-a434-00044be64e82","691d27fe-e3b1-11e9-9dd5-00044be64e82","6b3d216e-e3b7-11e9-bd3e-00044be64e82","6d147dce-e3bd-11e9-9307-00044be64e82","6fbe5424-e3b2-11e9-ad57-00044be64e82","760b70b6-e3c4-11e9-a84f-00044be64e82","766dcc22-e3b3-11e9-838a-00044be64e82","7ce86dcc-e3b4-11e9-afd3-00044be64e82","7ed780fe-e3ba-11e9-811b-00044be64e82","815ad7d2-e3af-11e9-833f-00044be64e82","87dac1c8-e3c1-11e9-a028-00044be64e82","87fc00ba-e3b0-11e9-90d1-00044be64e82","8e9d017a-e3b1-11e9-a7fc-00044be64e82","9294d3c8-e3bd-11e9-8ffd-00044be64e82","95499d8e-e3b2-11e9-9160-00044be64e82","9b8b507c-e3c4-11e9-9c33-00044be64e82","9beade90-e3b3-11e9-8ddd-00044be64e82","a273412a-e3b4-11e9-a2b1-00044be64e82","a462f100-e3ba-11e9-a46f-00044be64e82","a699e1c6-e3c0-11e9-ba4e-00044be64e82","a6e6419e-e3af-11e9-91a5-00044be64e82","aad1cb50-e3bb-11e9-854c-00044be64e82","ad88b710-e3b0-11e9-9a45-00044be64e82","b42899cc-e3b1-11e9-a17a-00044be64e82","b6536cd0-e3b7-11e9-a104-00044be64e82","b8232702-e3bd-11e9-93c4-00044be64e82","bac5e176-e3b2-11e9-9a5f-00044be64e82","c1176290-e3c4-11e9-94c7-00044be64e82","c1676ff8-e3b3-11e9-ad54-00044be64e82","c7f167ba-e3b4-11e9-8a74-00044be64e82","cc18cbb0-e3c0-11e9-96d7-00044be64e82","cc66333e-e3af-11e9-82c4-00044be64e82","ce8c82e8-e3b5-11e9-ad70-00044be64e82","d04f33d6-e3bb-11e9-859d-00044be64e82","d30670f4-e3b0-11e9-bcd0-00044be64e82","d9a5d6ec-e3b1-11e9-b2ea-00044be64e82","dbd305b0-e3b7-11e9-91ee-00044be64e82","e04fcba0-e3b2-11e9-b225-00044be64e82","e252636c-e3b8-11e9-85b7-00044be64e82","e6a3d2dc-e3c4-11e9-8168-00044be64e82","ed452fa4-e3c5-11e9-8bfc-00044be64e82","ed7cc240-e3b4-11e9-8701-00044be64e82","f41890ce-e3b5-11e9-9446-00044be64e82","f8857d16-e3b0-11e9-a7ca-00044be64e82","faa5f2be-e3b6-11e9-9a6c-00044be64e82","fc6ccc3e-e3bc-11e9-bebc-00044be64e82","ff253aa2-e3b1-11e9-a0a3-00044be64e82")
  val res: Future[List[Int]] = Future.sequence(list.map(imageUUID => {
    val imgAgg = ImageAggregated(imageUUID, s"/kerb/images/77e5afa2-d882-11e9-994a-00044be64e82/$imageUUID")
    ConnectionProvider.readPathAndSaveToDir(imgAgg)
  }))

  Thread.sleep(60000)
//  list.foreach{imageUUID =>
//    val imgAgg = ImageAggregated(imageUUID, s"/kerb/images/77e5afa2-d882-11e9-994a-00044be64e82/$imageUUID")
//    ConnectionProvider.readPathAndSaveToDir(imgAgg)
//  }
}