package com.zaiou.cleaning.service


import java.io.IOException

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, HTable, Table}
import org.apache.hadoop.hbase.mapreduce.TableInputFormat

import scala.collection.mutable

/**
  * @Description:
  * @author zaiou 2019-06-12
  * @modify zaiou 2019-06-12
  */
object HbaseService {
  def getHbaseConf(propertiesMap: mutable.Map[String, String],tableName: String): Configuration = {
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.property.clientPort", propertiesMap("spark.zk_port"))
    conf.set("hbase.zookeeper.quorum", propertiesMap("spark.zk_ip"))
    conf.set("zookeeper.znode.parent", "/hbase")
    conf.set(TableInputFormat.INPUT_TABLE, tableName) //ShortListCustNo  ShortListIdCard
    conf
  }

  def getHTable(propertiesMap: mutable.Map[String, String],tableNames: String): Table = {
    val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.property.clientPort", propertiesMap("spark.zk_port"))
    conf.set("hbase.zookeeper.quorum", propertiesMap("spark.zk_ip"))
    conf.set("zookeeper.znode.parent", "/hbase/master")
    val conn = ConnectionFactory.createConnection(conf)
    val tableName = TableName.valueOf(tableNames)
    conn.getTable(tableName)
  }
}
