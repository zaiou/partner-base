package com.zaiou.cleaning.job.merge

import com.zaiou.cleaning.common.Job
import org.apache.spark.sql.SQLContext

/**
  * @Description:
  * @author zaiou 2019-06-12
  * @modify zaiou 2019-06-12
  */
object MergeJob {

}

case class MergeJobConfig(fullPath:String=null,incrementPath:String=null,key: String=null,date:String=null,cloumeName:String=null)

class MergeJob(t:MergeJobConfig) extends Job[MergeJobConfig]{

  override val appName: String = null

  override def run(sqlContext: SQLContext, config: MergeJobConfig): Unit = {

  }
}