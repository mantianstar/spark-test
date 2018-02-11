package com.jumao.spark

import com.jumao.java.util.{MD5Util, MySqlUtil}
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable

/**
  * Created by Administrator on 2018/1/30.
  */
class DailyPriceIndustry extends TxtToHbase{
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
    // 取同一个组装的key对应的最大时间还没解决

    if(colValList.size>=16 && colValList(12).length>=8){
      val goodsCate = colValList(14).trim
      if(industryMap.containsKey(goodsCate)){
        val classCode:String = industryMap.get(colValList(14)).getIndustryCode
        // industryId，cateId，date，goodsCate，value，upAndDown，range，estimates
        val filterColVallist = List(colValList(0),colValList(1),colValList(2),colValList(3),colValList(4),colValList(5),
          colValList(6),colValList(7),colValList(8),colValList(9),colValList(10),colValList(11),
          colValList(12),colValList(13),colValList(14),classCode,colValList(15))
        return putDataForHBase(cf,getRowKey(colValList),colNameList,filterColVallist)
      }else {
        return (null,null)
      }
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
    // 为了实现分页按照时间倒序的前台展示：：这里用公元3000年的时间减去数据时间的值，拼接到rowkey后面：：使数据按照天倒序
    val sort:Int = 30000101 - colValList(12).replace("-", "").toInt
    val goodsCate = colValList(14).trim
    if(industryMap.containsKey(goodsCate)){
      val classCode:String = industryMap.get(colValList(14)).getIndustryCode
      // rowkey组成规则：
      val key = MD5Util.MD5(classCode).substring(0, 8).concat(classCode).concat(sort.toString).concat(colValList(6))
        .concat(colValList(10)).concat(colValList(4))
      return key
    }else {
      return null
    }
  }
}

object DailyPriceIndustry {
  def main(args: Array[String]): Unit = {
    val appName = "DailyPriceIndustry"
    val hbaseTabName = "test:dailyPriceList"
    val colNameList = List("goodsname","industrycode","industryname","oldcodename","companysmall","company","pricetype","price",
      "unit","spec","specsamll","area","date","oldonename","oldtwoname","classCode","timestamp")
    val relaPath = "/price/pricelist/pricelist.txt"

    val dailyPriceIndustry = new DailyPriceIndustry
    dailyPriceIndustry.readTxtAndSave(appName,hbaseTabName,colNameList,relaPath)
  }
}