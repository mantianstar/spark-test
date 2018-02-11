package com.jumao.spark

import com.jumao.java.util.DateUtils
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

/**
  * Created by Administrator on 2018/2/9.
  */
class DailyChangeList extends TxtToHbase{
  /**
    * 准备列值数据
    *
    * @param cf
    * @param colNameList
    * @param colValList
    * @return
    */
  override def preDealData(cf: String, colNameList: List[String], colValList: List[String]): (ImmutableBytesWritable, Put) = {
    val createTime = DateUtils.getFormatDate(DateUtils.TIME_FORMAT)
    val result = colValList(1)
    val rls = result.substring(0,result.length-1)
    val filterColVallist = List(createTime,rls)
    return putDataForHBase(cf,getRowKey(colValList),colNameList,filterColVallist)
  }

  /**
    * 获取rowKey值
    *
    * @param colValList
    * @return
    */
  override def getRowKey(colValList: List[String]): String = {
    val time = colValList(0).substring(2, colValList(0).length)
    val date = DateUtils.getNextDay(time, 0, DateUtils.TIME_FORMAT, DateUtils.DAY_FORMAT_YYYYMMDD)
    val industryCode = getIndustryCode(colValList(0).substring(0, 2))
    industryCode + date
  }

  /**
    * 匹配公司分类
    * @param name
    * @return
    */
  def  getIndustryCode(name: String) = name match {
    case "ys" => "02"
    case "gt" => "03"
    case "hg" => "04"
    case "nf" => "05"
    case "ny" => "06"
    case _ => ""
  }
}

object DailyChangeList{
  def main(args: Array[String]): Unit = {
    val appName = "DailyChangeList"
    val hbaseTabName = "test:dailyChangeList"
    val colNameList = List("createTime","result")
    val relaPath = "/home/price/changelist/changeListInPpi.txt"

    val dailyChangeList = new DailyChangeList
    dailyChangeList.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}