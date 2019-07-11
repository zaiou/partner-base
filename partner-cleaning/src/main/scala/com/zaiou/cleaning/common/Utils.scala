package com.zaiou.cleaning.common

import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

/**
  * @Description:
  * @author zaiou 2019-06-14
  * @modify zaiou 2019-06-14
  */
object Utils {
  def year_month_day(yyyymmdd: String) = {
    val sdf = new SimpleDateFormat("yyyyMMdd")
    sdf.parse(yyyymmdd)
  }

  def year_month_day(time: Date) = {
    val sdf = new SimpleDateFormat("yyyyMMdd")
    sdf.format(time)
  }

  def wrapString(colValue: String) = {
    if ("".equals(colValue) || "null".equals(colValue) || colValue == null) {
      None
    } else {
      Some(String.valueOf(colValue))
    }
  }

  def wrapLong(colValue: String) = {
    if ("".equals(colValue) || "null".equals(colValue) || colValue == null) {
      None
    } else {
      Some(java.lang.Long.parseLong(colValue))
    }
  }

  def wrapInt(colValue: String) = {
    if ("".equals(colValue) || "null".equals(colValue) || colValue == null) {
      None
    } else {
      Some(java.lang.Double.parseDouble(colValue).toInt)
    }
  }

  def wrapDouble(colValue: String) = {
    if ("".equals(colValue) || "null".equals(colValue) || colValue == null) {
      None
    } else {
      Some(java.lang.Double.parseDouble(colValue))
    }
  }

  val formatter = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.")

  def wrapTimestamp(colValue: String) = {
    if ("".equals(colValue) || "null".equals(colValue) || colValue == null) {
      None
    } else {
      Some(new java.sql.Timestamp(formatter.parse(colValue).getTime))
    }
  }


  def nextDay(current: Date) = {
    val c = Calendar.getInstance
    c.setTime(current)
    c.add(Calendar.DAY_OF_MONTH, 1)
    c.getTime
  }

  def md5(str: String) = {
    val digest = MessageDigest.getInstance("MD5")
    digest.digest(str.getBytes).map(0xFF & _).map("%02x".format(_)).mkString
  }
  /**
    * 根据日期获取当前日期所属季度
    *
    * @param date
    * @return
    */
  def getCurrentSeasonByDate(date: Date): String = {
    var season = 0
    val c = Calendar.getInstance
    c.setTime(date)
    val month = c.get(Calendar.MONTH)
    month match {
      case Calendar.JANUARY =>
      case Calendar.FEBRUARY =>
      case Calendar.MARCH =>
        season = 1
      case Calendar.APRIL =>
      case Calendar.MAY =>
      case Calendar.JUNE =>
        season = 2
      case Calendar.JULY =>
      case Calendar.AUGUST =>
      case Calendar.SEPTEMBER =>
        season = 3
      case Calendar.OCTOBER =>
      case Calendar.NOVEMBER =>
      case Calendar.DECEMBER =>
        season = 4
    }
    return String.valueOf(season)
  }
  //计算两个日期之间的天数
  def getBetweenDays(endDate: Long, beginDate: Long): Long = {
    val cal = Calendar.getInstance()
    cal.setTime(new Date(endDate));
    val end = cal.getTimeInMillis()
    cal.setTime(new Date(beginDate));
    val begin = cal.getTimeInMillis()
    val between_days = (end - begin) / (1000 * 3600 * 24)
    between_days
  }

  import java.util.Calendar

  /**
    * 获取两个日期相差的月数
    *
    * @param d1 较大的日期
    * @param d2 较小的日期
    * @return 如果d1>d2返回 月数差 否则返回0
    */
  def getMonthDiff(d1: Date, d2: Date): Int = {
    val c1 = Calendar.getInstance
    val c2 = Calendar.getInstance
    c1.setTime(d1)
    c2.setTime(d2)
    if (c1.getTimeInMillis < c2.getTimeInMillis) return 0
    val year1 = c1.get(Calendar.YEAR)
    val year2 = c2.get(Calendar.YEAR)
    val month1 = c1.get(Calendar.MONTH)
    val month2 = c2.get(Calendar.MONTH)
    val day1 = c1.get(Calendar.DAY_OF_MONTH)
    val day2 = c2.get(Calendar.DAY_OF_MONTH)
    // 获取年的差值 假设 d1 = 2015-8-16  d2 = 2011-9-30
    var yearInterval = year1 - year2
    // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
    if (month1 < month2 || month1 == month2 && day1 < day2) {
      yearInterval -= 1; yearInterval + 1
    }
    // 获取月数差值
    var monthInterval = (month1 + 12) - month2
    if (day1 < day2) {
      monthInterval -= 1; monthInterval + 1
    }
    monthInterval %= 12
    yearInterval * 12 + monthInterval
  }

  def main(args: Array[String]): Unit = {
    val d2 = Utils.year_month_day("19900101")
    println(getMonthDiff(new Date(),d2))
  }
}
