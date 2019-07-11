package com.zaiou.cleaning.job.parse.xd

import com.zaiou.cleaning.common.Inputs
import com.zaiou.cleaning.job.parse.ParseConfig
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import scopt.OptionParser

/**
  * @Description:借款合同信息
  * @author zaiou 2019-06-13
  * @modify zaiou 2019-06-13
  */
object DbContInfoParseJob {
  def main(args: Array[String]) = {
    val parser = new OptionParser[ParseConfig]("DbcontinfoParseJob") {
      head("DbcontinfoParseJob", "Spark")
      opt[String]('y', "year") required() action { (x, c) =>
        c.copy(year = x)
      } text ("year")
      opt[String]('m', "month") required() action { (x, c) =>
        c.copy(month = x)
      } text ("month")
      opt[String]('d', "day") required() action { (x, c) =>
        c.copy(day = x)
      } text ("day")
      help("help") text ("Print parameter description document")
    }

    parser.parse(args, ParseConfig()) map { config =>
      val app = new DbContInfoParseJob
      app.execute(config)
    } getOrElse {
      println("参数异常")
    }

  }
}

class DbContInfoParseJob[T] extends XdParse[ParseConfig] {
  override val appName: String = "DbcontinfoParseJob"
  override val table: String = "DBCONTINFO"
  override val columnSize: String = "98"
  override val mapping: List[(Int, String, String)] = List()


  override def run(sqlContext: SQLContext, config: ParseConfig): Unit = {
    val sc = sqlContext.sparkContext
    val file = s"${input}/${table}/${config.year}/${config.month}/${config.day}"

    val hadoopConf = new Configuration
    hadoopConf.set("column.size", columnSize)
    hadoopConf.set("column.delimiter", delimiter)

    val tableFile = sc.newAPIHadoopFile(file, classOf[TextInputFormat], classOf[LongWritable], classOf[Text], hadoopConf)
      .map(pair => (pair._1, new String(pair._2.getBytes, 0, pair._2.getLength, charseName)))
    printLog("DBCONTINFO:" + tableFile.count())

    val parseRows = tableFile.map(p => parseRow(p._1.get(), p._2, "")).cache()

    val errorPath = s"${output}/${table}/error/${config.year}/${config.month}/${config.day}"

    errorRow(sqlContext,parseRows,errorPath)

    val rowRdd=parseRows.filter(_.length==mapping.size)

    printLog("准备向hbase保存【借款合同信息表(DBCONTINFO)】数据 开始")

//    saveHbase(sqlContext,rowRdd)

    printLog("准备向hbase保存【借款合同信息表(DBCONTINFO)】数据 结束")
  }

  def saveHbase (sqlContext: SQLContext,rowRdd:RDD[Row]): Unit ={
    val dataFrame=sqlContext.createDataFrame(rowRdd,getSchema())
    val rs=dataFrame.withColumn(Inputs.BANK_CODE,dataFrame("bankid").substr(0,6))

    rs.registerTempTable("rs")

    val rowKeyRs=sqlContext.sql("select a.*," +
      s"a.contno as rowKey " +
      "from rs a")

    loadDataFrameToHbase(propertiesMap, sqlContext,rowKeyRs,output ,Inputs.XD_DBCONTINFO,"rowKey",getHbaseJobConf(propertiesMap,Inputs.XD_DBCONTINFO))

  }
}
