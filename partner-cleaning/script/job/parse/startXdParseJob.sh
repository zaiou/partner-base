#!/usr/bin/env bash
source /data/partner/partner-cleaning/script/util/util.sh

year=$1
month=$2
day=$3

ymd=`date +%Y%m%d`

#用户定义表
echo "importDataToMysqlByOriginal ===== ODS.AHXD_TMP_CMUSER_1208_TXT.txt 用户定义表"
hadoop fs -test -e $original_prefix/ftp/xd/CMUSER/$year/$month/$day/ODS.AHXD_TMP_CMUSER_1208_TXT.txt
if [ $? = 0 ]; then
    printlnLog "ODS.AHXD_TMP_CMUSER_1208_TXT.txt 【$year/$month/$day】文件存在，进行import至mysql"
    importDataToMysqlByOriginal "$original_prefix/ftp/xd/CMUSER/$year/$month/$day/ODS.AHXD_TMP_CMUSER_1208_TXT.txt" "DATA_XD_CMUSER" "," "USERID,BANKID,USERNAME" $year $month $day
    java -Djava.ext.dirs=$local_path/lib/run/ -cp $local_path/partner-cleaning-0.0.1-SNAPSHOT.jar com.zaiou.cleaning.redis.JedisSet "partner:cleaning:CmUserParseJob:$ymd" "true" "604800"
else
    printlnLog "ODS.AHXD_TMP_CMUSER_1208_TXT.txt【$year/$month/$day/】文件不存在"
fi

echo "借款合同信息表 DBCONTINFO"
hadoop fs -test -e $original_prefix/ftp/xd/DBCONTINFO/$year/$month/$day/ODS.AHXD_TMP_DBCONTINFO_1208_TXT.txt
if [ $? = 0 ]; then
    printlnLog "ODS.AHXD_TMP_DBCONTINFO_1208_TXT.txt 【$year/$month/$day】 文件存在，进行解析"
    printlnLog "DbContInfoParseJob"
    spark-submit \
        --master yarn \
        --deploy-mode client \
        --driver-memory $driverMemory \
        --executor-memory $executorMemory \
        --executor-cores $executorCores \
        --num-executors $numExecutors \
        --conf spark.ui.port=5011 \
        --conf spark.default.parallelism=$parallelism \
        --conf spark.yarn.executor.memoryOverhead=$memoryOverhead \
        --properties-file $local_path/config/cleaning.properties \
        --jars $local_path/lib/run/scopt_2.10-3.2.0.jar,$local_path/lib/run/mysql-connector-java-5.1.47.jar \
        --class com.zaiou.cleaning.job.parse.xd.DbContInfoParseJob $local_path/partner-cleaning-0.0.1-SNAPSHOT.jar --year $year --month $month --day $day >> $log_path/startDbContInfoParseJob.log
else
    printlnLog "ODS.AHXD_TMP_DBCONTINFO_1208_TXT.txt【$year/$month/$day/】文件不存在"
fi