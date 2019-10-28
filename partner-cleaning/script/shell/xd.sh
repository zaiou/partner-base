#!/bin/sh
source /data/partner/partner-cleaning/script/util/util.sh

year=$1
month=$2
day=$3
hour=$4
sys_name=$5
business_name=$6
total_sys_name="AHXD"
printlnLog "=====xd.sh======start=====year:$year==month:$month==day:$day==sys_name:$sys_name==name:$total_sys_name==bussiness_name:$business_name"

#ftp下载工作目录 -- 存储阶段性数据
hdfs_staging_prefix="$staging_prefix/ftp/xd"
hadoop fs -test -e $hdfs_staging_prefix
if [ $? != 0 ]; then
    hadoop fs -mkdir -p $hdfs_staging_prefix
fi

#创建parse文件夹
hadoop fs -test -e $hdfs_staging_prefix/parse
if [ $? != 0 ]; then
    hadoop fs -mkdir -p $hdfs_staging_prefix/parse
    hadoop fs -touchz $hdfs_staging_prefix/parse/$year$month$day.parse
fi

#创建ok文件夹
hadoop fs -test -e $hdfs_staging_prefix/ok
if [ $? != 0 ]; then
    hadoop fs -mkdir -p $hdfs_staging_prefix/ok
fi


#ftp下载文件存储到hdfs目录 -- 存储最终下载文件
hdfs_original_prefix="$original_prefix/ftp/xd"
hadoop fs -test -e $hdfs_original_prefix
if [ $? != 0 ]; then
    hadoop fs -mkdir -p $hdfs_original_prefix
fi

beginTime=`date +%s`

#xd[信贷系统]
declare -A fileMap=()
#用户定义表
fileMap["CMUSER"]="Y,USERID,BANKID";
#借款合同信息表
fileMap["DBCONTINFO"]="Y,CONTNO"

#当前的下载时间
downYear=`date +%Y -d "-$yesDay days"`
downMonth=`date +%m -d "-$yesDay days"`
downDay=`date +%d -d "-$yesDay days"`
downHour=`date +%H -d "-$yesDay days"`

#判断传入的参数为ftp,则执行抓取ftp昨天的数据
if [ $business_name = "ftp" ]; then

    #执行抓取任务
    for key in ${!fileMap[@]} ; do
        sh $local_path/script/shell/common/processTable.sh $key $hdfs_staging_prefix $hdfs_original_prefix $downYear $downMonth $downDay $downHour ${fileMap[$key]} "DATA_LOAD_" $sys_name $total_sys_name
    done
    endTime=`date +%s`
    printlnLog "xd耗时计算>>processTable耗时:"+$[endTime-beginTime]"秒"

    #判断文件是否下载完成
    okCount=`hadoop fs -count $hdfs_staging_prefix/ok/$downYear$downMonth$downDay/ | awk -F " " '{print $2}' `
    printlnLog "系统表文件数量:"${#fileMap[@]}" 下载文件数量:"$okCount
    if [ "$okCount" != "${#fileMap[@]}" ]; then
        printlnLog "OK文件个数不一致, 【$downYear$downMonth$downDay】文件未下载完毕"
    else
        printlnLog "OK文件个数一致, 【$downYear$downMonth$downDay】文件下载完毕"
    fi
fi

#判断传入的参数为parse，则执行解析hdfs上的文件
if [ $business_name = "parse" ]; then
    #获取hdfs目录上传时间
    fileParseTime=`hadoop fs -ls $hdfs_staging_prefix/parse/ | awk -F "/" 'NR==2{print $NF}'`
    printlnLog "循环解析->hdfs目录上的时间文件："$fileParseTime
    parseDate=`echo ${fileParseTime:0:8}`
    printlnLog "循环解析->准备解析目标文件时间："$parseDate
    #对获取的时间加一
    parseDateAdd=$(date -d "${parseDate} +1 day" +%Y%m%d)
    printlnLog "循环解析->准备解析目标文件时间+1："$parseDateAdd
    #判断时间是否为空
    if [ $parseDate ]; then
        printlnLog "解析日期不为空==parse==$parseDate"
        okCount=`hadoop fs -count $hdfs_staging_prefix/ok/$parseDate/ | awk -F " " '{print $2}'`
        if [ $okCount = "${#fileMap[@]}" ]; then
            printlnLog "系统表文件数量："${fileMap[@]}" 下载完成文件数量：$okCount OK文件个数一致，【$parseDate】文件下载完成，执行解析任务!"
            #获取解析时间
            parseYear=`echo ${parseDate:0:4}`
            parseMonth=`echo ${parseDate:4:2}`
            parseDay=`echo ${parseDate:6:2}`

            sh $local_path/script/job/parse/startXdParseJob.sh $parseYear $parseMonth $parseDay
            endParseTime=`date +%s`
            printlnLog "耗时时间>>startXdParseJob耗时:"$[$endParseTime-$beginTime]"秒"

#            #判断生成宽表的ok文件是否存在
#            hadoop fs -test -e $hdfs_staging_prefix/ok/$parseDate/ODS.AHXD_TMP_DBCONTINFO_1208_TXT_$parseDate.ok
#            DBCONTINFO=$?
#
#            if [[ $DBCONTINFO -eq 0 ]]; then
#                printlnLog "com.zaiou.cleaning.job.wide.XdWideJob"
#                #信贷合同宽表
#                spark-submit \
#                    --master yarn \
#                    --deploy-mode client \
#                    --driver-memory $driverMemory \
#                    --executor-memory $executorMemory \
#                    --executor-cores $executorCores \
#                    --num-executore $numExecutors \
#                    --conf spark.ui.port=5011 \
#                    --conf spark.default.parallelism=$parallelism \
#                    --conf spark.yarn.executor.memoryOverhead=$memoryOverhead \
#                    --properties-file $local_path/config/cleaning.properties \
#                    --jars $local_path/lib/run/scopt_2.10-3.2.0.jar,$local_path/lib/run/mysql-connector-java-5.1.47.jar \
#                    --class com.zaiou.cleaning.job.wide.XdContWideJob $local_path/partner-cleaning-0.0.1-SNAPSHOT.jar --year $parseYear --month $parseMonth --day $parseDay >> $log_path/startXdContWideHJob.log
#
#            fi
        else
           printlnLog "系统表文件数量:"${#fileMap[@]}" 下载完成文件数量:$okCount OK文件个数不一致，【$parseDate】文件未下载完毕,不执行解析任务！"
        fi
    else
       printlnLog "解析日期为空==parseDate==$parseDate"
    fi
fi



