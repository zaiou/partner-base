package com.zaiou.cleaning.job.parse.xd

import com.zaiou.cleaning.common.Paths
import com.zaiou.cleaning.job.parse.Parse

/**
  * @Description:
  * @author zaiou 2019-06-13
  * @modify zaiou 2019-06-13
  */
abstract class XdParse[T] extends Parse[T] {
  override val sysName: String = "xd"

  override val input: String = s"${Paths.FTP_LAYER}/xd"

  override val output: String = s"${Paths.WAREHOUSE_LAYER}/xd"

  val charseName = UTF8
}
