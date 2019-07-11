package com.zaiou.cleaning.common

/**
  * @Description:
  * @author zaiou 2019-06-11
  * @modify zaiou 2019-06-11
  */
object Paths {
  val HDFS_BASE = "/user/cleaning"
  val WAREHOUSE_LAYER = s"${HDFS_BASE}/warehouse"
  val ORIGINARL_LAYER = s"${HDFS_BASE}/original"
  val WIDE_LAYER = s"${WAREHOUSE_LAYER}/wide"
  val FTP_LAYER = s"${ORIGINARL_LAYER}/ftp"
}
