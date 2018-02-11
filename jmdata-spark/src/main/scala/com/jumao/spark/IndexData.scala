package com.jumao.spark

import com.jumao.java.bean.IndustryBean
import com.jumao.java.util.{MD5Util, MySqlUtil}
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

/**
  * Created by Administrator on 2018/1/29.
  */
class IndexData extends TxtToHbase{
  // 从mysql获取行业值
  val industryMap = MySqlUtil.getIndustryMap
  /**
    * 准备列值数据
    *
    * @param cf
    * @param colNameList
    * @param colValList
    * @return
    */
  override def preDealData(cf: String, colNameList: List[String], colValList: List[String]): (ImmutableBytesWritable, Put) = {
    if(colValList.size>=6){
      val goodsCate = colValList(0).trim
      if(industryMap.containsKey(goodsCate)){
        val industry:IndustryBean = industryMap.get(colValList(0).trim)
        val cateId:String = industry.getIndustryCode
        val industryId = cateId.split("-")(0)
        // industryId，cateId，date，goodsCate，value，upAndDown，range，estimates
        val filterColVallist = List(industryId,cateId,colValList(1),colValList(0),colValList(2),colValList(3),colValList(4),colValList(5))
        return putDataForHBase(cf,getRowKey(colValList),colNameList,filterColVallist)
      } else {
        return (null,null)
      }
    }else {
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
    // rowkey组成规则：行业ID+商品类别ID+日期
    MD5Util.MD5(colValList(0)).substring(0,8).concat(colValList(1)).concat(colValList(2).replaceAll("-",""))
  }
}

object IndexData {
  def main(args: Array[String]): Unit = {
    val appName = "IndexData"
    val hbaseTabName = "test:dailyIndex"
    val colNameList = List("industryId","cateId","date","goodsCate","value","upAndDown","range","estimates")
    val relaPath = "/index/industry/"

    val indexData = new IndexData
    indexData.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}
