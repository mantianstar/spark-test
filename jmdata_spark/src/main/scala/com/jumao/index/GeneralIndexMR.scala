package com.jumao.index

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by Administrator on 2018/1/26.
  */
case class HBaseRecord(col0: String,col1: String,col2: String,col3: String,col4: String)

object HBaseRecord
{
  def apply(col1: String, col2: String, col3: String, col4: String): HBaseRecord = {
//    val rowkey = MD5Util.MD5(col1).substring(0, 8) + col2
    val rowkey = col1 + col2
    HBaseRecord(rowkey,col1,col2,col3,col4)
  }
}

object GeneralIndexMR {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext(new SparkConf().setAppName("HBaseTest"))
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "172.18.203.141")
    conf.set("hbase.zookeeper.property.clientPort", "2181")

    var jobConf = new JobConf(conf)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, "test:generalData")

    val rdd = sc.textFile("hdfs://172.18.203.141:8020/jmhdfs/jss/index/general/")
              .map(_.split("\t"))
              .map(line=>putDataToHBase(line(0),line(1),line(2),line(3)))

    rdd.saveAsHadoopDataset(jobConf)
    sc.stop()
  }

  def putDataToHBase(col1:String, col2:String, col3:String, col4:String):(ImmutableBytesWritable,Put) = {
    val key = col1+col2
    val put = new Put(Bytes.toBytes(key))
    put.addColumn("info".getBytes, "createDate".getBytes, col1.getBytes)
    put.addColumn("info".getBytes, "indexName".getBytes, col2.getBytes)
    put.addColumn("info".getBytes, "indexValue".getBytes, col3.getBytes)
    put.addColumn("info".getBytes, "publicDate".getBytes, col4.getBytes)
    (new ImmutableBytesWritable, put)
  }

}
