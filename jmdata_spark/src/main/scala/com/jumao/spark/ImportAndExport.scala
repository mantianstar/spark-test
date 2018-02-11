package com.jumao.spark

import com.jumao.java.util.{MD5Util, MySqlUtil}
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

/**
  * Created by Administrator on 2018/1/30.
  */
class ImportAndExport extends TxtToHbase{

  val cateMap = MySqlUtil.getAllCategories
  /**
    * 准备列值数据
    *
    * @param cf
    * @param colNameList
    * @param colValList
    * @return
    */
  override def preDealData(cf: String, colNameList: List[String], colValList: List[String]): (ImmutableBytesWritable, Put) = {
    val cateId = colValList(0).trim
    if(colValList.size>=9 && cateMap.containsKey(cateId)){
      val category = cateMap.get(cateId)
      // createDate，publicDate，indexName，indexValue,rank
      val filterColVallist = List(cateId,category.getCateName,category.getpId(),colValList(1),colValList(2),colValList(3)
        ,colValList(4),colValList(5),colValList(6),colValList(7),colValList(8),colValList(9))
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
    // rowkey组成规则：商品类别ID+商品类别ID+年份，
    MD5Util.additiveHash(colValList(0),13).toString.concat(colValList(0)).concat(colValList(1))
  }
}

object ImportAndExport{
  def main(args: Array[String]): Unit = {
    val appName = "ImportAndExport"
    val hbaseTabName = "test:importAndExport"
    val colNameList = List("cateId","cateName","pId","year","impAmount","impQuantity","expAmount","expQuantity",
      "impAmountUnit","impQuantityUnit","expAmountUnit","expQuantityUnit")
    val relaPath = "/export/ImpAndExp.txt"

    val importAndExport = new ImportAndExport
    importAndExport.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}
