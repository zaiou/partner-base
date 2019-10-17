package test

import java.util.Date

import com.zaiou.cleaning.common.Inputs
import com.zaiou.cleaning.service.HbaseService
import org.apache.hadoop.fs.{FsShell, Path}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, HTable, Put}
import org.apache.hadoop.hbase.{HBaseConfiguration, KeyValue, TableName}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2
import org.apache.hadoop.hbase.tool.LoadIncrementalHFiles
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.mapreduce.Job
import org.apache.spark.SparkConf
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{DoubleType, IntegerType, LongType, StringType, StructField, StructType}
import org.json

import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, ListBuffer}

/**
  * @Description:
  * @author zaiou 2019-09-15
  * @modify zaiou 2019-09-15
  */
class HdfsTest extends Serializable {

//  @org.junit.Test
  def test3():Unit={
    val a="你好"
    val b=Some(a)
    println("--------------------")
    val conf = new SparkConf().setMaster("local")
    conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    conf.set("spark.shuffle.consolidateFiles", "true")
    conf.set("spark.sql.sources.partitionColumnTypeInference.enabled","false")
    conf.set("spark.sql.shuffle.partitions", "8")

    conf.set("spark.zk_port", "2181")
    conf.set("spark.zk_ip", "localhost")

    val sparkSession = SparkSession.builder().appName("RDD to DataFrame")
      .config(conf).getOrCreate()

    sparkSession.sparkContext.setLocalProperty("spark.scheduler.pool", "default")

    println("--------------------")
    val structFields = Array(StructField("id",StringType,true),StructField("name",StringType,true))
    val structType = StructType(structFields)
    val lines= sparkSession.sparkContext .textFile("/user/test/textdata.txt")
    val rdd = lines.map(_.split(",")).map(x=>Row(x(0),x(1)))
//    val rdd = lines.map(_.split(",")).map(x=>Row.fromSeq(x))
    println("+++++++++++++"+rdd.first())
    val df = sparkSession.createDataFrame(rdd,structType)
//    print("---"+df.first())
    println("=============")

    df.createOrReplaceTempView("rs")
    val rowKeyRs=sparkSession.sql("select a.*," +
      s"a.id as rowKey " +
      "from rs a")

    val dropDuplicates = new ArrayBuffer[String]
    dropDuplicates.+=("rowKey")
    val dropDuplicatesRowKeyRs = rowKeyRs.dropDuplicates(dropDuplicates.toArray)

    val schema = rowKeyRs.schema
    val columnSort = schema.map(_.name).sorted
    val columnMap = mutable.Map[String, String]()
    schema.foreach(t => {
      columnMap.+=(t.name -> t.dataType.typeName)
    })
    val mapRowKeyRs = dropDuplicatesRowKeyRs.rdd.map(r => {
      packageParseKeyValue(r,columnSort,columnMap,"rowKey")
    })

//    println("+++++++&&"+mapRowKeyRs.first())

    val filter=mapRowKeyRs.filter(_._1!=null)
    val flatMap=filter.sortByKey().flatMap(_._2)
//    println("&&%%%"+flatMap.first())

    val output="/user/cleaning/warehouse/xd"
    val pathTable = "XD:DbContInfo".replaceAll(":", "_")
    val  propertiesMap=mutable.Map[String,String]()


    sparkSession.sparkContext.getConf.getAll.foreach(t=>{
      propertiesMap.+=(t._1->t._2)
    })

    println("propertiesMap:"+propertiesMap)

    println("保存数据到hdfs"+s"hdfs://localhost:9000${output}/hFile/${pathTable}")
    flatMap.saveAsNewAPIHadoopFile(s"hdfs://localhost:9000${output}/hFile/${pathTable}", classOf[ImmutableBytesWritable], classOf[KeyValue], classOf[HFileOutputFormat2], getHbaseJobConf(propertiesMap,Inputs.XD_DBCONTINFO)) //.filter(_._1 != null)
    println("保存hdfs成功")
    val shell=new FsShell(HBaseConfiguration.create())
    shell.run(Array("-chmod","-R","777",s"hdfs://localhost:9000${output}/hFile/${pathTable}"))
    shell.close()

    doBulkLoad(s"hdfs://localhost:9000${output}/hFile/${pathTable}", "XD:DbContInfo", getHbaseJobConf(propertiesMap,Inputs.XD_DBCONTINFO))

    lazy val hbaseSchema=HbaseService.getHTable(propertiesMap,Inputs.HBASE_SCHEMA)

    println("===hbaseSchema："+hbaseSchema)

    val oldJson = Bytes.toString(hbaseSchema.get(new Get(Bytes.toBytes("XD:DbContInfo"))).getValue(Bytes.toBytes("f"), Bytes.toBytes("schema")))

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
    val p=new Put(Bytes.toBytes("XD:DbContInfo"))
    p.addColumn(Bytes.toBytes("f"),Bytes.toBytes("schema"),Bytes.toBytes(StructType(schemaList.distinct.toList).json))
    hbaseSchema.put(p)
    hbaseSchema.close()
    //Row类看看
    //需要确定本地存储文件到hdfs为什么需要加hdfs前缀，服务器不用； 当为本地时 前缀加file://是否为本地文件，服务器上可是
    //测试服务上面的文件直接能用服务器地址能不能查看
  }
//  @Test
  def test2():Unit= {
//    val listBuffer = new ListBuffer[Any]
    var a = "2"
//    listBuffer+=a
//    val b = Some(123456).toString
//    print(Bytes.toBytes(a.reverse))
//    println(new ImmutableBytesWritable(Bytes.toBytes(a.reverse)))
  }

  def doBulkLoad(path: String, tableName: String, hBaseConf: JobConf) = {
    println("BulkLoad-保存数据到hbase开始")
    val load = new LoadIncrementalHFiles(hBaseConf)
    val conn = ConnectionFactory.createConnection(hBaseConf)
    val table = conn.getTable(TableName.valueOf(tableName))
    println("table:"+table)
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

  def getHbaseJobConf(propertiesMap:mutable.Map[String,String],tableName: String):JobConf={
    val jobConf = new JobConf(HBaseConfiguration.create())
    jobConf.set("hbase.zookeeper.property.clientPort", "2181")
    jobConf.set("hbase.zookeeper.quorum", "localhost")
    jobConf.set("zookeeper.znode.parent", "/hbase/master")
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf.set("hbase.mapreduce.hfileoutputformat.table.name", tableName)
    jobConf.set(TableOutputFormat.OUTPUT_TABLE, tableName) //ShortListCustNo  ShortListIdCard
    jobConf.setInt("hbase.mapreduce.bulkload.max.hfiles.perRegion.perFamily", 1024)
    jobConf
  }

  def matchStructFieldByHbase(colName: String, colType: String): StructField = colType match {
    case "string" => StructField(colName, StringType, false)
    case "long" => StructField(colName, LongType, false)
    case "integer" => StructField(colName, IntegerType, false)
    case "double" => StructField(colName, DoubleType, false)
    case _ => StructField(colName, StringType, false)
  }

  def packageParseKeyValue(r: Row, columnSort: Seq[(String)], columnMap: mutable.Map[String, String], pRowKey: String) = {
    val kvs = new ArrayBuffer[(ImmutableBytesWritable, KeyValue)]
    var ibw: ImmutableBytesWritable = null
    try {
      var rk: Array[Byte] = null
      val rowKey = r.getAs[String](pRowKey)
      rk = Bytes.toBytes(rowKey.reverse)
      ibw = new ImmutableBytesWritable(rk)
//      println("----"+ibw)
//      println("&&&&&&"+columnSort)
//      println(columnMap)
      columnSort.foreach(c => {
        if (!"rowKey".equals(c)) {
          if (r.getAs[Object](c) != null) {
            val kv = new KeyValue(rk, Bytes.toBytes("f"), Bytes.toBytes(c), new Date().getTime, matchBytes(columnMap(c), c, r))
//            println("+++++++"+kv)
            kvs.+=((ibw, kv))
//            println("+++"+kvs)
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

  def matchBytes(colType: String, name: String, r: Row) = colType match {
    case "string" => Bytes.toBytes(String.valueOf(r.getAs[String](name)))
    case "long" => Bytes.toBytes(new java.math.BigDecimal(r.getAs[Long](name)).toPlainString())
    case "integer" => Bytes.toBytes(new java.math.BigDecimal(r.getAs[Int](name)).toPlainString())
    case "double" => Bytes.toBytes(new java.math.BigDecimal(r.getAs[Double](name)).toPlainString())
    case _ => Bytes.toBytes(String.valueOf(r.getAs[Any](name)))
  }

}
