package com.jumao.spark

import java.text.SimpleDateFormat

import com.jumao.java.util.MySqlUtil
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

/**
  * Created by Administrator on 2018/2/9.
  */
class CommodityPriceList extends TxtToHbase {
  /**
    * 准备列值数据
    *
    * @param cf
    * @param colNameList
    * @param colValList
    * @return
    */
  override def preDealData(cf:String,colNameList:List[String],colValList:List[String]):(ImmutableBytesWritable,Put)={
    return putDataForHBase(cf,getRowKey(colValList),colNameList,List(colValList.mkString))
  }

  /**
    * 获取rowKey值
    *
    * @param colValList
    * @return
    */
  override def getRowKey(colValList: List[String]): String = {
    // rowkey组成规则：返回时间戳
    val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    format.parse(colValList(0)).getTime.toString
  }
}

object CommodityPriceList{
  def main(args: Array[String]): Unit = {
    val appName = "CommodityPriceList"
    val hbaseTabName = "test:commodityPriceList"
    val colNameList = List("val")
    val relaPath = "/home/jmi/index.txt"

    val commodityPriceList = new CommodityPriceList
    commodityPriceList.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}
