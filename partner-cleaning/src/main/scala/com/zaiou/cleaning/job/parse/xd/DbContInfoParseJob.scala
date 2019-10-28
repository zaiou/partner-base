package com.zaiou.cleaning.job.parse.xd

import com.zaiou.cleaning.common.Inputs
import com.zaiou.cleaning.job.parse.ParseConfig
import com.zaiou.cleaning.utils
import com.zaiou.cleaning.utils.PLog
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Row, SQLContext}
import scopt.OptionParser

/**
  * @Description:借款合同信息
  * @author zaiou 2019-06-13
  * @modify zaiou 2019-06-13
  */
object DbContInfoParseJob{
  def main(args: Array[String]) = {
    val parser = new OptionParser[ParseConfig]("DbcontinfoParseJob") {
      head("DbcontinfoParseJob", "Spark")
      opt[String]('y', "year") required() action { (x, c) =>
        c.copy(year = x)
      } text ("year")
      opt[String]('m', "month") required() action { (x, c) =>
        c.copy(month = x)
      } text ("month")
      opt[String]('d', "day") required() action { (x, c) =>
        c.copy(day = x)
      } text ("day")
      help("help") text ("Print parameter description document")
    }

    parser.parse(args, ParseConfig()) map { config =>
      val app = new DbContInfoParseJob
      app.execute(config)
    } getOrElse {
      println("参数异常")
    }

  }
}

class DbContInfoParseJob[T] extends XdParse[ParseConfig]{
  override val appName: String = "DbcontinfoParseJob"
  override val table: String = "DBCONTINFO"
  override val columnSize: String = "98"
  override def run(sqlContext: SQLContext, config: ParseConfig): Unit = {
    val sc = sqlContext.sparkContext
    val file = s"${input}/${table}/${config.year}/${config.month}/${config.day}"
    val hadoopConf = new Configuration
    hadoopConf.set("column.size", columnSize)
    hadoopConf.set("column.delimiter", delimiter)

    PLog.logger.info("-----获取hdfs文件"+file)

    val tableFile = sc.newAPIHadoopFile(file, classOf[TextInputFormat], classOf[LongWritable], classOf[Text], hadoopConf)
      .map(pair => (pair._1, new String(pair._2.getBytes, 0, pair._2.getLength, charseName)))
    PLog.logger.info("DBCONTINFO:" + tableFile.count())

    val parseRows = tableFile.map(p => parseRow(p._1.get(), p._2, "")).cache()

    val errorPath = s"${output}/${table}/error/${config.year}/${config.month}/${config.day}"

    errorRow(sqlContext,parseRows,errorPath)

    val rowRdd=parseRows.filter(_.length==mapping.size)

    PLog.logger.info("准备向hbase保存【借款合同信息表(DBCONTINFO)】数据 开始")

    saveHbase(sqlContext,rowRdd)

    PLog.logger.info("准备向hbase保存【借款合同信息表(DBCONTINFO)】数据 结束")
  }

  def saveHbase (sqlContext: SQLContext,rowRdd:RDD[Row]): Unit ={

    val dataFrame=sqlContext.createDataFrame(rowRdd,getSchema())

    val rs=dataFrame.withColumn(Inputs.BANK_CODE,dataFrame("bankid").substr(0,6))
    rs.createOrReplaceTempView("rs")
    val rowKeyRs=sqlContext.sql("select a.*," +
      s"a.contno as rowKey " +
      "from rs a")

    loadDataFrameToHbase(propertiesMap, sqlContext,rowKeyRs,output ,Inputs.XD_DBCONTINFO,"rowKey",getHbaseJobConf(propertiesMap,Inputs.XD_DBCONTINFO))

  }

  override val mapping: List[(Int, String, String)] = List(
    (0, "contno", "String"),
    (2, "custid", "String"),
//    (3, "custname", "String"),
//    (4, "prodid", "String"),
//    (5, "prodname", "String"),
//    (6, "conttype", "String"),
//    (7, "currsign", "String"),
//    (8, "apprtotalamt", "Double"),
//    (9, "conttotalamt", "Double"),
//    (10, "conttouseamt", "Double"),
//    (12, "apptterm", "String"),
//    (13, "contterm", "String"),
//    (14, "advismaxuseterm", "Double"),
//    (15, "begindate", "String"),
//    (16, "enddate", "String"),
//    (17, "assukind", "String"),
//    (18, "loanuseremark", "String"),
//    (20, "fundput", "String"),
//    (22, "isagriflag", "String"),
//    (24, "riskflag", "String"),
//    (28, "funds", "String"),
//    (30, "issetcostrate", "String"),
//    (36, "useamtkind", "String"),
//    (39, "indesubsacno", "String"),
//    (40, "indesubsac", "String"),
//    (41, "retukind", "String"),
//    (42, "intebalkind", "String"),
//    (44, "subsflag", "String"),
//    (47, "depesubsacno", "String"),
//    (48, "depesubsac", "String"),
//    (56, "dispresokind", "String"),
//    (61, "signdate", "String"),
//    (62, "contstatus", "String"),
      (63, "bankid", "String")
//    (64, "operid", "String"),
//    (69, "Paymode", "String"),
//    (72, "YidaiNo", "String"),
//    (73, "LoanForm", "String"),
//    (79, "post", "String"),
//    (80, "mobile", "String"),
//    (81, "loanuse", "String"),
//    (85, "ReleChannel", "String"),
//    (87, "RecdId", "String"),
//    (90, "liableperson", "String")
//    (93, "isSubsFlag", "String")


    /**
      * DBCONTINFO	借款合同信息表 全部字段
      *
      * 0	合同编号	contno
      * 1	业务编号	loanid
      * 2	客户编号	custid
      * 3	客户名称	custname
      * 4	产品编号	prodid
      * 5	产品名称	prodname
      * 6	借款合同类型	conttype    //1 非循环  2：循环
      * 7	币种	currsign
      * 8	批准金额（元）	apprtotalamt
      * 9	合同金额（元）	conttotalamt
      * 10	合同可用金额（元）	conttouseamt
      * 11	期限单位	termunit
      * 12	批准期限	apptterm
      * 13	合同期限	contterm
      * 14	最长用款期限	advismaxuseterm
      * 15	合同起始日期	begindate
      * 16	合同到期日期	enddate
      * 17	贷款方式	assukind  10：信用 20：保证 30：抵押 40：质押 51：抵押+保证 52：质押+抵押 53：质押+保证 54：抵押+质押+保证
      * 18	贷款用途说明	loanuseremark
      * 19	还款来源说明	retusourreamrk
      * 20	贷款投向行业代码	fundput
      * 21	贷款投向行业说明	fundputremark
      * 22	是否涉农	isagriflag
      * 23	涉农用途说明	isagridesc
      * 24	贷款质量	riskflag
      * 25	贷款质量说明	riskremark
      * 26	贷款经营类型	loanmanagtp
      * 27	政策扶持方式	govsuptype
      * 28	资金来源	funds
      * 29	是否指定借款人用款计划	issetuseloanplan
      * 30	是否指定费率信息	issetcostrate
      * 31	授信额度使用标识	creduseflag
      * 32	银团贷款标识	bankgrpflag
      * 33	银团贷款项目名称	bankgrpprojname
      * 34	本行参团方式	bankgrpjointype
      * 35	银团贷款总额（元）	bankgrploanamt
      * 36	用款方式	useamtkind
      * 37	自主支付限额	indepaylim
      * 38	受托支付下限	depepaylowlim
      * 39	自主支付账号	indesubsacno
      * 40	自主支付户名	indesubsac
      * 41	还款方式	retukind
      * 42	结息方式	intebalkind
      * 43	计息方式	intecompkind
      * 44	扣款方式	subsflag
      * 45	约定扣款日期	subsdate
      * 46	转逾期宽限天数	turngraceday
      * 47	委托扣款账号	depesubsacno
      * 48	委托扣款户名	depesubsac
      * 49	第三方还款账号	thirdpartyacno
      * 50	第三方还款户名	thirdpartyac
      * 51	利息罚息计算标志	sbFineFlag
      * 52	是否贴息	Interestflag
      * 53	贴息比例	Inteperc
      * 54	贴息起始日	InterestBegindate
      * 55	贴息到期日	InterestEnddate
      * 56	争议解决方式	dispresokind
      * 57	仲裁委员会名称	arbicommname
      * 58	仲裁地点	arbiarea
      * 59	放款前提条件	loanprecond
      * 60	其他约定事项	otheragre
      * 61	合同签订日期	signdate
      * 62	合同状态	contstatus  A0：合同新增 A1：合同录入 A2：合同审查中 A3：合同审查完毕 A4：合同生效 A5：合同冻结 A6：合同终止 A7：合同执行完毕 A8：合同解冻审批中
      * 63	经办机构	bankid
      * 64	经办人	operid
      * 65	最后修改人	lastchanperson
      * 66	最后修改机构	Lastchanbankid
      * 67	最后修改时间	Lastchandate
      * 68	最后修改日期	lastchangetime
      * 69	支付方式	Paymode
      * 70	受托支付账户	Stsubsacno
      * 71	受托支付户名	Stsubsac
      * 72	易贷卡卡号	YidaiNo
      * 73	贷款形式	LoanForm
      * 74	上次分类日期	oldsortdate
      * 75	上次分类结果	oldriskflag
      * 76	分类日期	sortdate
      * 77	机评结果	autoriskflag
      * 78	地址	addr
      * 79	邮编	post
      * 80	联系电话	mobile
      * 81	贷款用途	loanuse
      * 82	不良贷款移交标志	badrecflag
      * 83	清收机构	collbankid
      * 84	清收员	colloperid
      * 85	放款渠道	ReleChannel
      * 86	是否本行账户	isownbanksubsacno
      * 87	推荐人	RecdId
      * 88	推荐机构	RecdBankId
      * 89	贷款账号	loanacno
      * 90	主责任人	liableperson
      * 91	利率调整方式	InteAdjuKind
      * 92	逾期利率浮动比例	floatinterate
      * 93	是否扣款	isSubsFlag
      * 94	收息方式	approachkind
      * 95	精准扶贫标识	JZFPFLAG
      * 96	脱贫状态	poortype
      * 97	第三方扣款账号	discountacno

      */

    /**
      * create table "dbContInfo"
      * (
      * "contno" VARCHAR(80) not null,
      * "loanid" VARCHAR(60),
      * "custid" VARCHAR(22),
      * "custname" VARCHAR(80),
      * "prodid" VARCHAR(20),
      * "prodname" VARCHAR(80),
      * "conttype" CHAR(1),
      * "currsign" CHAR(3),
      * "apprtotalamt" NUMERIC(16,2),
      * "conttotalamt" NUMERIC(16,2),
      * "conttouseamt" NUMERIC(16,2),
      * "termunit" CHAR(1),
      * "apptterm" NUMERIC(10),
      * "contterm" NUMERIC(10),
      * "advismaxuseterm" NUMERIC(10),
      * "begindate" DATE,
      * "enddate" DATE,
      * "assukind" CHAR(2),
      * "loanuseremark" VARCHAR(500),
      * "retusourreamrk" VARCHAR(500),
      * "fundput" VARCHAR(10),
      * "fundputremark" VARCHAR(250),
      * "isagriflag" CHAR(1),
      * "isagridesc" CHAR(2),
      * "riskflag" CHAR(2),
      * "riskremark" VARCHAR(250),
      * "loanmanagtp" CHAR(2),
      * "govsuptype" CHAR(2),
      * "funds" CHAR(2),
      * "issetuseloanplan" CHAR(1),
      * "issetcostrate" CHAR(1),
      * "creduseflag" CHAR(1),
      * "bankgrpflag" CHAR(1),
      * "bankgrpprojname" VARCHAR(80),
      * "bankgrpjointype" CHAR(1),
      * "bankgrploanamt" NUMERIC(16,2),
      * "useamtkind" CHAR(2),
      * "indepaylim" NUMERIC(16,2),
      * "depepaylowlim" NUMERIC(16,2),
      * "indesubsacno" VARCHAR(50),
      * "indesubsac" VARCHAR(100),
      * "retukind" CHAR(2),
      * "intebalkind" CHAR(1),
      * "intecompkind" CHAR(1),
      * "subsflag" CHAR(1),
      * "subsdate" VARCHAR(10),
      * "turngraceday" NUMERIC(5),
      * "depesubsacno" VARCHAR(50),
      * "depesubsac" VARCHAR(100),
      * "thirdpartyacno" VARCHAR(30),
      * "thirdpartyac" VARCHAR(100),
      * "sbFineFlag" CHAR(1),
      * "Interestflag" CHAR(1),
      * "Inteperc" NUMERIC(6,2),
      * "InterestBegindate" DATE,
      * "InterestEnddate" DATE,
      * "dispresokind" CHAR(1),
      * "arbicommname" VARCHAR(40),
      * "arbiarea" VARCHAR(100),
      * "loanprecond" VARCHAR(300),
      * "otheragre" VARCHAR(300),
      * "signdate" DATE,
      * "contstatus" CHAR(2),
      * "bankid" VARCHAR(12),
      * "operid" VARCHAR(10),
      * "lastchanperson" VARCHAR(10),
      * "Lastchanbankid" VARCHAR(12),
      * "Lastchandate" TIMESTAMP,
      * "lastchangetime" TIMESTAMP,
      * "Paymode" CHAR(2),
      * "Stsubsacno" VARCHAR(30),
      * "Stsubsac" VARCHAR(100),
      * "YidaiNo" VARCHAR(30),
      * "LoanForm" CHAR(1),
      * "oldsortdate" DATE,
      * "oldriskflag" VARCHAR(2),
      * "sortdate" DATE,
      * "autoriskflag" VARCHAR(2),
      * "addr" VARCHAR(80),
      * "post" VARCHAR(6),
      * "mobile" VARCHAR(11),
      * "loanuse" VARCHAR(2),
      * "badrecflag" char(2),
      * "collbankid" char(12),
      * "colloperid" varchar(10),
      * "ReleChannel" VARCHAR(50),
      * "isownbanksubsacno" char(1),
      * "RecdId" VARCHAR(20),
      * "RecdBankId" VARCHAR(20),
      * "loanacno" char(30),
      * "liableperson" varchar(60),
      * "InteAdjuKind" CHAR(1),
      * "floatinterate" DECIMAL(6,2),
      * "isSubsFlag" varchar（2）,
      * "approachkind" VARCHAR(10),
      * JZFPFLAG CHAR(1),
      * "poortype" CHAR(1),
      * "discountacno" VARCHAR(30),
      * constraint "P_Key_1" primary key ("contno")
      * );
      */
  )
}
