package com.zaiou.cleaning.job.wide

import com.zaiou.cleaning.common.{Hbase, Paths}
import org.apache.spark.sql.SQLContext
import scopt.OptionParser

/**
  * @Description:信贷合同宽表
  * @author zaiou 2019-06-11
  * @modify zaiou 2019-06-11
  */
object XdContWideJob {
  def main(args: Array[String]): Unit = {
    val parser = new OptionParser[XdContWideJobConfig]("XdContWideJob") {
      head("XdContWideJob", "Spark")
      opt[String]('y', "year") required() action { (x, c) =>
        c.copy(year = x)
      } text ("year")
      opt[String]('m', "month") required() action { (x, c) =>
        c.copy(month = x)
      } text ("month")
      opt[String]('d', "day") required() action { (x, c) =>
        c.copy(day = x)
      } text ("day")
      help("help") text ("print partmer description document")
    }

    parser.parse(args,XdContWideJobConfig())map{ config=>
      val  app= new XdContWideJob
    }
  }
}

case class XdContWideJobConfig(year: String = null, month: String = null, day: String = null)

class XdContWideJob extends WideJob[XdContWideJobConfig] with Hbase {
  override val appName:String="XdContWideJob"

  override val sysName: String = "xd"

  override val OUTPUT: String = s"${Paths.WIDE_LAYER}/xd/ContWideTemp"

  override def run(sqlContext: SQLContext, config: XdContWideJobConfig): Unit = {
    sqlContext.udf.register("longToString",longToString  _)
    //合同信息

  }
}