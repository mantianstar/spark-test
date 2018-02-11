package com.jumao.spark

import com.jumao.java.util.MD5Util
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

/**
  * Created by Administrator on 2018/1/30.
  */
class DailyPriceRank extends TxtToHbase{
  /**
    * 准备列值数据
    *
    * @param cf
    * @param colNameList
    * @param colValList
    * @return
    */
  override def preDealData(cf: String, colNameList: List[String], colValList: List[String]): (ImmutableBytesWritable, Put) = {
    if(colValList.size>=10 && colValList(6).length>=8){
      // createDate，publicDate，indexName，indexValue,rank
      val filterColVallist = List(colValList(0),colValList(1),colValList(2),colValList(3),colValList(4),colValList(5)
        ,colValList(6),colValList(7),colValList(8),colValList(9))
      return putDataForHBase(cf,getRowKey(colValList),colNameList,filterColVallist)
    } else {
      return (null,null)
    }
  }

  /**
    * 获取rowKey值
    *
    * @param colValList
    * @return
    */
  override def getRowKey(colValList: List[String]): String = {
    // rowkey组成规则：行业md5+行业+日期+排名+商品名称
    MD5Util.MD5(colValList(1)).substring(0,8).concat("_").concat(colValList(1)).concat("_").concat(colValList(6))
      .concat("_").concat(colValList(8)).concat("_").concat(colValList(0))
  }
}

object DailyPriceRank{
  def main(args: Array[String]): Unit = {
    val appName = "DailyPriceRank"
    val hbaseTabName = "test:dailyPriceRank"
    val colNameList = List("goodsname","industrycode","industryname","oldcodename","price","updown","date","showdate",
      "sort","timestamp")
    val relaPath = "/price/pricerank/pricerank.txt"

    val dailyPriceRank = new DailyPriceRank
    dailyPriceRank.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}
