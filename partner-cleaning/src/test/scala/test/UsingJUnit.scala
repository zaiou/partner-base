package test

import com.typesafe.scalalogging.Logger
import com.zaiou.cleaning.utils.PLog
import com.zaiou.common.enums.ResultInfo
import com.zaiou.common.exception.BussinessException
import lombok.extern.slf4j.Slf4j
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.{KeyValueTextInputFormat, TextInputFormat}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.{Row, SQLContext}
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.junit
import org.junit.Test
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * @Description:
  * @author zaiou 2019-08-18
  * @modify zaiou 2019-08-18
  */
@Test
@Slf4j
class UsingJUnit extends Serializable{

  @junit.Test
  def test:Unit={

    throw new BussinessException(ResultInfo.CLEANING_3000.getCode(), ResultInfo.CLEANING_3000.getMsg())
    System.exit(1)

    val logger = LoggerFactory.getLogger(this.getClass)
    try {
      PLog.logger.debug("***")
      val a = 1 / 0
      logger.info("This is very convenient ;-)")
    } catch {
      case e: Exception=> {
        logger.error(e.getMessage,e)
      }
    }
  }

//  @Test
  def testList(): Unit = {
    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)
    conf.set("column.delimiter", ",")
    conf.set("column.size", "98")

    val job = new Job()
//    val data = sc.textFile("/Users/liubin/data/test")
    val data = sc.newAPIHadoopFile("hdfs://localhost:9000/user/test" ,
      classOf[TextInputFormat],
      classOf[LongWritable],
      classOf[Text],
      job.getConfiguration)
        .map(pair => (pair._1, new String(pair._2.getBytes, 0, pair._2.getLength, "utf-8")))
//    data.foreach(println)
    println("-----"+data.count())
    data.foreach(p=>println(p._1+"-----"+p._2))
  }


//  @Test
  def testList1(): Unit = {
    var str="0000150000.00,+00000000150000.00,\"2\",+0000000036.,+0000000036.,+0000000012.,20180428,20210427,\"20\",\"家庭消费\",\"家庭收入\",\"O8010\",\"居民服务、修理和其他服务业-居民服务业-家庭服务\",\"0\",\"  \",\"11\",\"\",\"    \",\"  \",\"1 \",\"0\",\"0\",\"1\",\"0\",\"\",\" \",,\"02\",+00000000000000.00,+00000000000000.00,\"6217788314502062471\",\"黄开群\",\"90\",\"2\",\" \",\"3\",\"\",+00000.,\"6217788314502062471\",\"黄开群\",\"\",\"\",\"0\",\"0\",,,,\"2\",\"阜阳市仲裁委员会\",\"阜阳市\",\"无\",\"\",,\"A4\",\"3410453875\",\"387524\",\"387524\",\"3410453875\",\"2018-04-28-00.00.00.000000\",\"2018-04-28-15.56.10.000000\",\"1 \",\"\",\"\",\"6217788314502062471\",\"1\",,,,,\"\",\"\",\"\",\"06\",,,,\"15,11,14,16,17,18,22,24,19\",,\"387524\",\"3410453875\",,\"387524\",,,\"\",,\"0\",,,,"
    str=str.replaceAll(" ", "")
    val arr=str.split(",", -1)
    for (tuple3 <- mapping) {
      var colValue = arr(tuple3._1).replaceAll("\"", "").replaceAll("'", "")
    }

  }

//  @Test
  def test2():Unit={
    val conf = new SparkConf().setAppName("WordCount").setMaster("local")
    val sc = new SparkContext(conf)

    val data = sc.parallelize(Seq("Bern;10;12")) // mock for real data

    val schema = new StructType().add("city", StringType, true).add("female", IntegerType, true).add("male", IntegerType, true)

    val cities = data.map(line => {
      val Array(city,female,male) = line.split(";")
      Row(
        city,
        female.toInt,
        male.toInt
      )
    }
    )

    val citiesDF = new SQLContext(sc).createDataFrame(cities, schema)
  }



  val mapping: List[(Int, String, String)] = List(
    (0, "contno", "String"),
    (2, "custid", "String"),
    (3, "custname", "String"),
    (4, "prodid", "String"),
    (5, "prodname", "String"),
    (6, "conttype", "String"),
    (7, "currsign", "String"),
    (8, "apprtotalamt", "Double"),
    (9, "conttotalamt", "Double"),
    (10, "conttouseamt", "Double"),
    (12, "apptterm", "String"),
    (13, "contterm", "String"),
    (14, "advismaxuseterm", "Double"),
    (15, "begindate", "String"),
    (16, "enddate", "String"),
    (17, "assukind", "String"),
    (18, "loanuseremark", "String"),
    (20, "fundput", "String"),
    (22, "isagriflag", "String"),
    (24, "riskflag", "String"),
    (28, "funds", "String"),
    (30, "issetcostrate", "String"),
    (36, "useamtkind", "String"),
    (39, "indesubsacno", "String"),
    (40, "indesubsac", "String"),
    (41, "retukind", "String"),
    (42, "intebalkind", "String"),
    (44, "subsflag", "String"),
    (47, "depesubsacno", "String"),
    (48, "depesubsac", "String"),
    (56, "dispresokind", "String"),
    (61, "signdate", "String"),
    (62, "contstatus", "String"),
    (63, "bankid", "String"),
    (64, "operid", "String"),
    (69, "Paymode", "String"),
    (72, "YidaiNo", "String"),
    (73, "LoanForm", "String"),
    (79, "post", "String"),
    (80, "mobile", "String"),
    (81, "loanuse", "String"),
    (85, "ReleChannel", "String"),
    (87, "RecdId", "String"),
    (90, "liableperson", "String"),
    (93, "isSubsFlag", "String")
  )


}
