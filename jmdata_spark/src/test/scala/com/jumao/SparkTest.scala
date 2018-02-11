package com.jumao

import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.{SparkConf, SparkContext}
import org.junit.Assert.assertTrue
import org.junit.Test

/**
  * Created by Administrator on 2018/2/10.
  */
class SparkTest{




}
object SparkTest {
  def main(args: Array[String]): Unit = {
    println("aaa")
    val sc = new SparkContext(new SparkConf().setAppName("spark_test"))
/*    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.quorum", "172.18.203.141")
    conf.set("hbase.zookeeper.property.clientPort", "2181")
    var jobConf = new JobConf(conf)
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, "test:dailySpotFutures")*/


    /*def seq(a:Int, b:Int) : Int ={
      math.max(a,b)
    }

    def comb(a:Int, b:Int) : Int ={
      0
    }

    val data = sc.parallelize(List((1,3),(1,2),(1, 4),(2,3),(2,4)))

    val aa = data.aggregateByKey(3)(seq, comb)

    aa.foreach(x=>println(x))*/

    def minCurSeq(a:Array[String], b:Array[String]) : Array[String] ={
      val aCur = a(1).substring(2,a(1).length).toInt
      val bCur = b(1).substring(2,b(1).length).toInt
      if(bCur<aCur){
        return b
      }else{
        return a
      }
    }

    def minCurComb(a:Array[String],b:Array[String]):Array[String]={
      b
    }

    def maxHoldSeq(a:Array[String], b:Array[String]) : Array[String] ={
      val aCur = a(a.length-3).replace(",","").toInt
      val bCur = b(b.length-3).replace(",","").toInt
      if(bCur>aCur){
        return b
      }else{
        return a
      }
    }

    def maxHoldComb(a:Array[String],b:Array[String]):Array[String]={
      b
    }

    def extractValue(tuple:Tuple2[Array[String],Array[String]]):Array[String]={
      val (curRecord,holdRecord) = tuple
      val recentMonth = curRecord(1).substring(2, curRecord(1).length)
      val recentPrice = curRecord(6)
      val mainMonth = holdRecord(1).substring(2, holdRecord(1).length)
      val mainPrice = holdRecord(6)
      return Array(recentMonth,recentPrice,mainMonth,mainPrice)
    }

    val list = List(Array("20161029","CF611","15,015.00","14,980.00","15,070.00","14,980.00","15,070.00","15,020.00","55.00","5.00","22","2,036","-12","165.23"),
      Array("20161029","CF701","15,125.00","15,255.00","15,305.00","15,085.00","15,195.00","15,200.00","70.00","75.00","370,654","380,938","-39,236","2,817,413.46"))

    val data = sc.parallelize(list)
    val aa = data.map(x=>(1,x)).aggregateByKey(Array("20161029","CF612"))(minCurSeq,minCurComb)
    aa.foreach(x=>{println(x._2(0))})

    val bb = data.map(x=>(1,x)).aggregateByKey(Array("0","0","0","0","0","20161029","CF610"))(maxHoldSeq,maxHoldComb)
    bb.foreach(x=>{println(x._2.length)})

    val cc = aa.join(bb).map(record=>extractValue(record._2))

    cc.foreach(x=>println(x.mkString))

  }
}
