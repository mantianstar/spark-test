package com.jumao.spark

import com.jumao.java.util.MD5Util
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

/**
  * Created by Administrator on 2018/1/29.
  */
class TopIndex extends TxtToHbase{
  /**
    * 准备列值数据
    *
    * @param cf
    * @param colNameList
    * @param colValList
    * @return
    */
  override def preDealData(cf: String, colNameList: List[String], colValList: List[String]): (ImmutableBytesWritable, Put) = {
    if(colValList.size>=5){
      // createDate，publicDate，indexName，indexValue,rank
      val indexValue = colValList(1).concat(colValList(4))
      val filterColVallist = List(colValList(0),colValList(2),colValList(3),indexValue,colValList(5))
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
    // rowkey组成规则：日期+涨跌+排名
    MD5Util.MD5(colValList(0)).substring(0,8).concat(colValList(1)).concat(colValList(5))
  }
}

object TopIndex {
  def main(args: Array[String]): Unit = {
    val appName = "TopIndex"
    val hbaseTabName = "test:topIndex"
    val colNameList = List("createDate","publicDate","indexName","indexValue","rand")
    val relaPath = "/index/top/"

    val topIndex = new TopIndex
    topIndex.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}
