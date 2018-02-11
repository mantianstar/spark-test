package com.jumao.spark

/**
  * Created by Administrator on 2018/1/29.
  */
class GeneralIndex extends TxtToHbase {

}

object GeneralIndex{
  def main(args: Array[String]): Unit = {

    val appName = "GeneralIndex"
    val hbaseTabName = "test:generalData"
    val colNameList = List("createDate","indexName","indexValue","publicDate")
    val relaPath = "/index/general/"

    val generalIndex = new GeneralIndex
    generalIndex.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}
