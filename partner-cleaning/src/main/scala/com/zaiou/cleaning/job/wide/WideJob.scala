package com.zaiou.cleaning.job.wide

import com.zaiou.cleaning.common.{Job, Paths}

/**
  * @Description:
  * @author zaiou 2019-06-11
  * @modify zaiou 2019-06-11
  */
abstract class WideJob[T] extends Job[T] {

  override val sysName: String = "wide"

  val OUTPUT: String = s"${Paths.WIDE_LAYER}"
}
