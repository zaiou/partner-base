package com.zaiou.cleaning.common

import java.net.URI
import java.util.Date

import com.zaiou.cleaning.service.HbaseService
import com.zaiou.common.utils.StringUtils
import org.apache.hadoop.fs.{FileSystem, FsShell, Path}
import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, HTable, Put}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2
import org.apache.hadoop.hbase.tool.LoadIncrementalHFiles
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.types.{DoubleType, IntegerType, LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.json
import org.json.JSONObject

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * @Description:
  * @author zaiou 2019-06-11
  * @modify zaiou 2019-06-11
  */
trait Hbase extends Logging {

//  def hbaseToDataFrame(propertiesMap: mutable.Map[String, String], sqlContext: SQLContext, table: String): DataFrame = {
//    val hadoopRDD = sqlContext.sparkContext.newAPIHadoopRDD(HbaseService.getHbaseConf(propertiesMap, table), classOf[TableInputFormat], classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable], classOf[org.apache.hadoop.hbase.client.Result])
//
//    lazy val hbaseSchema = HbaseService.getHTable(propertiesMap, Inputs.HBASE_SCHEMA)
//    val json = Bytes.toString(hbaseSchema.get(new Get(Bytes.toBytes(table))).getValue(Bytes.toBytes("f"), Bytes.toBytes("schema")))
//    if (StringUtils.isEmpty(json)) {
//      throw new RuntimeException(s"表[${table}]的HBASE_SCHEMA不存在，请解析后在执行此JOB!!!")
//    }
//    println(s"table:${table}  jsonSchema:${json}")
//    val jsonArray = new JSONObject(json).getJSONArray("fields")
//    val schemaList = new ListBuffer[StructField]
//    schemaList += StructField("rowKey", StringType, false)
//    for (i <- 0 until jsonArray.length()) {
//
//    }
//
//    //待续
//    null
//  }

  def loadDataFrameToHbase(propertiesMap: mutable.Map[String, String], sqlContxt: SQLContext, rowKeyP: DataFrame, output: String, table: String, pRowKey: String, tableJobConf: JobConf) = {
    val schema = rowKeyP.schema
    var rowKeyRs = rowKeyP
    val dropDuplicates = new ArrayBuffer[String]

    dropDuplicates.+=(pRowKey)

    if (Inputs.CUST_IDCARD.eq(pRowKey) || Inputs.CUST_NO.equals(pRowKey) || Inputs.CONTRACT_NO.equals(pRowKey)) {
      dropDuplicates.+=(Inputs.BANK_CODE)
      rowKeyRs = rowKeyP.where(s"substring(${Inputs.BANK_CODE},0,2)='34'")
    }

    val pathTable = table.replaceAll(":", "_")

    val deleteRs = FileSystem.get(new URI(s"${output}/hFile"), sqlContxt.sparkContext.hadoopConfiguration).delete(new Path(s"${output}/hFile/${pathTable}"), true)
    println(s"${output}/hFile/${pathTable} deleteRs:${deleteRs}")

    val columnMap = mutable.Map[String, String]()
    schema.foreach(t => {
      columnMap.+=(t.name -> t.dataType.typeName)
    })
    val columnSort = schema.map(_.name).sorted
    println(s"loadDataFrameToHbase=======>dropDuplicates:${dropDuplicates.mkString(",")} columnSort:${columnSort} columnMap:${columnMap}")

    val dropDuplicatesRowKeyRs = rowKeyRs.dropDuplicates(dropDuplicates.toArray)

    val mapRowKeyRs = dropDuplicatesRowKeyRs.rdd.map(r => {
      packageParseKeyValue(r,columnSort,columnMap,pRowKey)
    })
    val filter=mapRowKeyRs.filter(_._1!=null)

    val flatMap=filter.sortByKey().flatMap(_._2)
    println("保存数据到hdfs"+s"${output}/hFile/${pathTable}")
    flatMap.saveAsNewAPIHadoopFile(s"${output}/hFile/${pathTable}", classOf[ImmutableBytesWritable], classOf[KeyValue], classOf[HFileOutputFormat2], tableJobConf)
    val shell=new FsShell(HBaseConfiguration.create())
    shell.run(Array("-chmod","-R","777",s"${output}/hFile/${pathTable}"))
    shell.close()

    println("=====保存到hdfs：hfile成功!")

    doBulkLoad(s"${output}/hFile/${pathTable}", table, tableJobConf)

    lazy val hbaseSchema=HbaseService.getHTable(propertiesMap,Inputs.HBASE_SCHEMA)

    println("===hbaseSchema："+hbaseSchema)

    val oldJson = Bytes.toString(hbaseSchema.get(new Get(Bytes.toBytes(table))).getValue(Bytes.toBytes("f"), Bytes.toBytes("schema")))

    val schemaList=new ListBuffer[StructField]
    schema.foreach(schemaList.+=(_))

    if (oldJson!=null){
      val oldJsonObject=new json.JSONObject(oldJson)
      if (oldJsonObject.has("fields")){
        val oldJsonArray=oldJsonObject.getJSONArray("fields")
        for (i<-0 until oldJsonArray.length()){
          val jsonObj=oldJsonArray.getJSONObject(i)
          schemaList+=matchStructFieldByHbase(jsonObj.getString("name"),jsonObj.getString("type"))
        }
      }
    }
    val p=new Put(Bytes.toBytes(table))
    p.addColumn(Bytes.toBytes("f"),Bytes.toBytes("schema"),Bytes.toBytes(StructType(schemaList.distinct.toList).json))
    hbaseSchema.put(p)
    hbaseSchema.close()
  }

  def packageParseKeyValue(r: Row, columnSort: Seq[(String)], columnMap: mutable.Map[String, String], pRowKey: String) = {
    val kvs = new ArrayBuffer[(ImmutableBytesWritable, KeyValue)]
    var ibw: ImmutableBytesWritable = null
    try {
      var rk: Array[Byte] = null
      if (Inputs.CUST_IDCARD.equals(pRowKey) || Inputs.CUST_NO.equals(pRowKey) || Inputs.CONTRACT_NO.equals(pRowKey)) {
        val bankCode = r.getAs[String](Inputs.BANK_CODE)
        val byVal = r.getAs[String](pRowKey)
        val rowKey = getShortListRowKey(bankCode, byVal)
        rk = Bytes.toBytes(rowKey)
      } else {
        val rowKey = r.getAs[String](pRowKey)
        rk = Bytes.toBytes(rowKey.reverse)
      }
      ibw = new ImmutableBytesWritable(rk)
      columnSort.foreach(c => {
        if (!"rowKey".equals(c)) {
          if (r.getAs[Object](c) != null) {
            val kv = new KeyValue(rk, Bytes.toBytes("f"), Bytes.toBytes(c), new Date().getTime, matchBytes(columnMap(c), c, r))
            kvs.+=((ibw, kv))
          }
        }
      })
    } catch {
      case e: Exception => {
        e.printStackTrace()
        println("packageParseKeyValue:" + e.getMessage)
      }
    }
    (ibw, kvs.toList)

  }


  def doBulkLoad(path: String, tableName: String, hBaseConf: JobConf) = {
    println("BulkLoad-保存数据到hbase开始")
    val load = new LoadIncrementalHFiles(hBaseConf)
    val conn = ConnectionFactory.createConnection(hBaseConf)
    val table = conn.getTable(TableName.valueOf(tableName))
    try {
      val regionLocator = conn.getRegionLocator(TableName.valueOf(tableName))
      val job = Job.getInstance(hBaseConf)
      job.setJobName(s"doBulkLoad[${tableName}]")
      job.setMapOutputKeyClass(classOf[ImmutableBytesWritable])
      job.setMapOutputValueClass(classOf[KeyValue])
      HFileOutputFormat2.configureIncrementalLoad(job, table, regionLocator)
      load.doBulkLoad(new Path(path),conn.getAdmin,table.asInstanceOf[HTable],regionLocator)
//      load.doBulkLoad(new Path(path), table.asInstanceOf[HTable])
    } finally {
      println("BulkLoad-保存数据到hbase结束")
      table.close()
      conn.close()
    }
  }

  def getShortListRowKey(bankCode: String, custNo: String) = {
    bankCode.substring(2, bankCode.length).reverse + custNo
  }

  def getHbaseJobConf(propertiesMap:mutable.Map[String,String],tableName: String):JobConf={
    val jobConf = new JobConf(HBaseConfiguration.create())
    jobConf.set("hbase.zookeeper.property.clientPort", propertiesMap("spark.zk_port"))
    jobConf.set("hbase.zookeeper.quorum", propertiesMap("spark.zk_ip"))
    jobConf.set("zookeeper.znode.parent", "/hbase/master")
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set("hbase.mapreduce.hfileoutputformat.table.name", tableName)
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, tableName) //ShortListCustNo  ShortListIdCard
    jobConf.setInt("hbase.mapreduce.bulkload.max.hfiles.perRegion.perFamily", 1024)
    jobConf
  }

  def matchBytes(colType: String, name: String, r: Row) = colType match {
    //    case "string" => Bytes.toBytes(String.valueOf(r.getAs[String](name)))
    case "long" => Bytes.toBytes(new java.math.BigDecimal(r.getAs[Long](name)).toPlainString())
    case "integer" => Bytes.toBytes(new java.math.BigDecimal(r.getAs[Int](name)).toPlainString())
    case "double" => Bytes.toBytes(new java.math.BigDecimal(r.getAs[Double](name)).toPlainString())
    case _ => Bytes.toBytes(String.valueOf(r.getAs[Any](name)))
  }

  def matchStructFieldByHbase(colName: String, colType: String): StructField = colType match {
    case "string" => StructField(colName, StringType, false)
    case "long" => StructField(colName, LongType, false)
    case "integer" => StructField(colName, IntegerType, false)
    case "double" => StructField(colName, DoubleType, false)
    case _ => StructField(colName, StringType, false)
  }
}
