package com.zaiou.cleaning.common

import java.text.SimpleDateFormat
import java.util.Date

import com.zaiou.cleaning.db.MysqlService
import com.zaiou.cleaning.utils.MD5
import com.zaiou.common.utils.{DateUtils, MD5Utils}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.hive.HiveContext

import scala.collection.mutable

/**
  * @Description:
  * @author zaiou 2019-06-11
  * @modify zaiou 2019-06-11
  */
abstract class Job[T] extends Serializable with Logging {
  val appName: String

  val sysName: String = "commons"

  def run(sqlContext: SQLContext, config: T)


  lazy val mysqlService=new MysqlService
  val  propertiesMap=mutable.Map[String,String]()

  def execute(config: T): Unit={
    val id=mysqlService.insertSysScheduleLog(s"cleaning${appName}")
    printLog(s"SysScheduleLog->id:${id}")

    System.setProperty("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val conf=new SparkConf().setAppName(appName)
    conf.set("spark.shuffle.consolidateFiles", "true")
    conf.set("spark.sql.sources.partitionColumnTypeInference.enabled","false")
    conf.set("spark.sql.shuffle.partitions", "8")
    val sc=new SparkContext(conf)
    sc.setLocalProperty("spark.scheduler.pool", "default")

    sc.getConf.getAll.foreach(t=>{
      propertiesMap.+=(t._1->t._2)
    })
    if (!"online".equals(propertiesMap.get("spark.model").get)) {
      printLog(s"propertiesMap:${propertiesMap}")
    }
    val sqlContext:SQLContext=new HiveContext(sc)
    sqlContext.udf.register("md5",MD5.MD51 _)
    run(sqlContext,config)

  }

  def printLog(info:String):Unit={
    println(longToString(System.currentTimeMillis(),DateUtils.format2)+"["+Thread.currentThread().getId+"]"+info)
  }

  def longToString(t:Long, format: String): String ={
    val sdf=new SimpleDateFormat(format)
    val d=new Date(t)
    sdf.format(d)
  }
}
