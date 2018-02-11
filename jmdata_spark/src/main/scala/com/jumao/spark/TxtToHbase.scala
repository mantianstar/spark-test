package com.jumao.spark

import com.jumao.java.util.MD5Util
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Administrator on 2018/1/29.
  */
trait TxtToHbase extends Serializable{
  /**
    * 读取文本并写入hbase
    * @param appName
    * @param hbaseTabName
    * @param colNameList
    * @param relaPath
    */
  def readTxtAndSave(appName:String,hbaseTabName:String,colNameList:List[String],relaPath:String):Unit={
    val sc = new SparkContext(new SparkConf().setAppName(appName))
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "172.18.203.141")
    conf.set("hbase.zookeeper.property.clientPort", "2181")

    var jobConf = new JobConf(conf)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, hbaseTabName)

    val cf = "info"

    val dataFilePath = "hdfs://172.18.203.141:8020/jmhdfs/jss".concat(relaPath)

    val rdd = dealRDD(sc,dataFilePath,colNameList,cf)
    //println(rdd.count())
    //rdd.foreach(println(_))
    rdd.saveAsHadoopDataset(jobConf)
    sc.stop()
  }

  /**
    *
    * @param sc
    * @param dataFilePath
    * @param colNameList
    * @param cf
    * @return
    */
  def dealRDD(sc:SparkContext,dataFilePath:String,colNameList:List[String],cf:String) = {
    sc.textFile(dataFilePath)
      .map(_.split("\t"))
      .map(line=>preDealData(cf,colNameList,line.toList))
      .filter(x=>x!=(null,null))
  }

  /**
    * 准备列值数据
    * @param cf
    * @param colNameList
    * @param colValList
    * @return
    */
  def preDealData(cf:String,colNameList:List[String],colValList:List[String]):(ImmutableBytesWritable,Put)={
    if(colValList.size>=4){
      return putDataForHBase(cf,getRowKey(colValList),colNameList,colValList)
    } else {
      return (null,null)
    }
  }

  /**
    * 获取rowKey值
    * @param colValList
    * @return
    */
  def getRowKey(colValList:List[String]):String={
    // rowkey组成规则：日期+指数名称
    MD5Util.MD5(colValList(0)).substring(0,8).concat(colValList(1))
  }

  /**
    * 根据列名，列值生成hbase需要的put对象
    * @param cf
    * @param rowKey
    * @param colNameList
    * @param colValList
    * @return
    */
  def putDataForHBase(cf:String,rowKey:String,colNameList:List[String], colValList:List[String]):(ImmutableBytesWritable,Put) = {
    if(rowKey!=null){
      // 设置rowkey
      val put = new Put(Bytes.toBytes(rowKey))
      // 设置列值
      colNameList.zip(colValList)
        .map(colPair=>{put.addColumn(cf.getBytes, colPair._1.getBytes, colPair._2.getBytes)})
      // 返回值
      (new ImmutableBytesWritable, put)
    }else{
      return (null,null)
    }
  }
}
