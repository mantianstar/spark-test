package com.jumao.spark

import com.jumao.java.util.{MD5Util, MySqlUtil, UnifyUtils}
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by Administrator on 2018/2/10.
  */
class DailyCZCE extends TxtToHbase {
  val map = MySqlUtil.getFuturesCodeByAbbr
  val DEF_NUM_SCALE = 2
  /**
    * 准备列值数据
    *
    * @param cf
    * @param colNameList
    * @param colValList
    * @return
    */
  override def preDealData(cf: String, colNameList: List[String], colValList: List[String]): (ImmutableBytesWritable, Put) = {
    val filterColVallist = colValList.drop(1)
    val rowKey = colValList(0)
    putDataForHBase(cf,rowKey,colNameList,filterColVallist)
  }


  override def dealRDD(sc: SparkContext, dataFilePath: String, colNameList: List[String], cf: String): RDD[(ImmutableBytesWritable, Put)] = {
    /**
      * 判断最近合约，规则：交割月份最小的
      * @param a
      * @param b
      * @return
      */
    def minCurSeq(a:Array[String], b:Array[String]) : Array[String] ={
      val aCur = a(1).substring(2,a(1).length).toInt
      val bCur = b(1).substring(2,b(1).length).toInt
      if(bCur<aCur) b else a
    }

    def minCurComb(a:Array[String],b:Array[String]):Array[String]= b

    /**
      * 判断持仓量最大的
      * @param a
      * @param b
      * @return
      */
    def maxHoldSeq(a:Array[String], b:Array[String]) : Array[String] ={
      val aCur = a(a.length-3).replace(",","").toInt
      val bCur = b(b.length-3).replace(",","").toInt
      if(bCur>aCur) b else a
    }

    def maxHoldComb(a:Array[String],b:Array[String]):Array[String]=b

    def extractValue(rowKey:String, tuple:Tuple2[Array[String],Array[String]]):Array[String]={
      val futuresPlatId = rowKey.substring(5,6)
      val futuresPlatIndex = rowKey.substring(14,16)
      val (curRecord,holdRecord) = tuple
      val recentMonth = curRecord(1).substring(2, curRecord(1).length)
      val recentPrice = curRecord(6)
      val mainMonth = holdRecord(1).substring(2, holdRecord(1).length)
      val mainPrice = holdRecord(6)
      return Array(rowKey,futuresPlatId,futuresPlatIndex,recentMonth,recentPrice,mainMonth,mainPrice)
    }

    // 根据规则的key生成pairRDD
    val pairRDD = sc.textFile(dataFilePath).map(_.replace("|","").split("\t")).map(x=>{
      if(x.length>=3){
        if(getRowKey(x.toList)==null){
          Tuple2(null,null)
        }else{
          Tuple2(getRowKey(x.toList),x)
        }
      }else{
        Tuple2(null,null)
      }

    }).filter(x=>x!=(null,null))
    /*pairRDD.foreach(x=>{println(x._2)})*/

    /*val pairRDD = sc.textFile(dataFilePath).map(_.split("\t")).map(x=>("1",x))
    pairRDD.foreach(x=>{println(x._2(1))})*/

    // 根据pairRDD产生最近合约的数据
    val curRDD = pairRDD.aggregateByKey(pairRDD.first()._2)(minCurSeq,minCurComb)
    // 根据pairRDD产生最大持仓的数据
    val holdRDD = pairRDD.aggregateByKey(pairRDD.first()._2)(maxHoldSeq,maxHoldComb)
    // 关联数据并处理成一个数组
    curRDD.join(holdRDD).map(record=>extractValue(record._1,record._2))
      .map(arr=>preDealData(cf,colNameList,arr.toList))
      .filter(x=>x!=(null,null))

  }

  /**
    * 获取rowKey值
    *
    * @param colValList
    * @return
    */
  override def getRowKey(colValList: List[String]): String = {
    val date = colValList(0)
    val name = colValList(1)
    val regex = "^[a-z0-9A-Z]+$"
    if(name.matches(regex)){
      val abbr = name.substring(0, 2)
      val fc = map.get(abbr)
      if (null != fc) {
        val plat = fc.getPlatId
        val platIndex = UnifyUtils.sameLength(fc.getPlatIndex, DEF_NUM_SCALE, "0")
        val key = plat.concat(date).concat(platIndex)
        // 第一个字母md5后截取5个
        val numMd5 = MD5Util.MD5(key.substring(0,1)).substring(0,5)
        val rowkey = numMd5.concat(key)
        return rowkey
      }
      return null
    }
    return null
  }
}

object DailyCZCE {
  def main(args: Array[String]): Unit = {
    val appName = "DailyCZCE"
    val hbaseTabName = "test:dailySpotFutures"
    val colNameList = List("futuresPlatId","futuresPlatIndex","latestContract","latestPrice","mainContract","mainPrice")
    val relaPath = "/home/comp/futureDataDailyInZqs.txt"

    val dailyCZCE = new DailyCZCE
    dailyCZCE.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}
