package com.zaiou.cleaning.job.parse

import java.net.URI

import com.zaiou.cleaning.common.{Hbase, Job, Utils}
import com.zaiou.cleaning.input.MyLineRecordReader
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DoubleType, IntegerType, LongType, StringType, StructField, StructType, TimestampType}
import org.apache.spark.sql.{Row, SQLContext}

import scala.collection.mutable.ListBuffer

/**
  * @Description:
  * @author zaiou 2019-06-13
  * @modify zaiou 2019-06-13
  */
abstract class Parse[T] extends Job[T] with Hbase {
  val UTF8 = "UTF-8"

  val GBK = "GBK"

  val delimiter = ","

  override val sysName: String = "parse"

  val table: String

  val mapping: List[(Int, String, String)]

  val columnSize: String

  val input: String

  val output: String

  def parseRow(lNo: Long, l: String, idCard: String): Row = {
    val ll = l.replaceAll(" ", "")
    val listBuffer = new ListBuffer[Any]
    try {
      if (columnSize.toInt == ll.split(delimiter, -1).length) {
        //正常方式解析数据
        parseRowByCommon(ll, listBuffer, idCard)
      } else {
        //处理引号中间的逗号，使之成为一个新的正常的行
        val newLine = MyLineRecordReader.preProcess(ll)
        //按照正常的方式去解析数据
        parseRowByCommon(newLine, listBuffer, idCard)
      }
    } catch {
      case e: Exception => {
        log.error(s"parseRow(line==>${l})", e)
        log.error(s"mapping==>${mapping.length}:${mapping.toArray.mkString(",")}")
        log.error(s"listBuffer==>${listBuffer.length}:${listBuffer.toArray.mkString(",")}")
        listBuffer.clear()
        listBuffer += Some(lNo, l)
      }
    }
    Row.fromSeq(listBuffer.toSeq)
  }

  def parseRowByCommon(line: String, listBuffer: ListBuffer[Any], idCard: String): ListBuffer[Any] = {
    listBuffer.clear()
    var arr = line.split(delimiter, -1)
    for (tuple3 <- mapping) {
      var colValue = arr(tuple3._1).replaceAll("\"", "").replaceAll("'", "")
      if (tuple3._2.equals(idCard)) {
        colValue = colValue.toUpperCase()
      }
      listBuffer += matchFieldType(colValue, tuple3._3)
    }

    listBuffer
  }

  def matchFieldType(colValue: String, colType: String): Any = colType match {
    case "String" => Utils.wrapString(colValue)
    case "Long" => Utils.wrapLong(colValue)
    case "Int" => Utils.wrapInt(colValue)
    case "Double" => Utils.wrapDouble(colValue)
    case "Timestamp" => Utils.wrapTimestamp(colValue)
    case _ => Utils.wrapString(colValue)
  }

  def errorRow(sqlContex:SQLContext,parseRows:RDD[Row],errorPath:String)={
    val error=parseRows.filter(_.length!=mapping.size)
    printLog(s"errorRow:${error.count()}")
    val deleteRs=FileSystem.get(new URI(errorPath),sqlContex.sparkContext.hadoopConfiguration).delete(new Path(s"{errorPath}"),true)
    printLog(s"${errorPath} deleteRs:${deleteRs}")
    error.saveAsTextFile(errorPath)
  }

  def getSchema():StructType={
    val listBuffer=new ListBuffer[StructField]
    for (tuple3<-mapping){
      listBuffer+=matchStructFieldByParse(tuple3._2,tuple3._3)
    }
    StructType(listBuffer.toList)
  }

  def matchStructFieldByParse(colName: String, colType: String): StructField = colType match {
    case "String" => StructField(colName, StringType, false)
    case "Long" => StructField(colName, LongType, false)
    case "Int" => StructField(colName, IntegerType, false)
    case "Double" => StructField(colName, DoubleType, false)
    case "Timestamp" => StructField(colName, TimestampType, false)
    case _ => StructField(colName, StringType, false)
  }


}

case class ParseConfig(year: String = null, month: String = null, day: String = null)
