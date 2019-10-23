#!/usr/bin/env bash

#获取当前操作系统
uname="echo `uname`"

#hdfs目录
cleaning_prefix="/user/cleaning"
staging_prefix="${cleaning_prefix}/staging" #ftp拉取数据，存储阶段性数据
original_prefix="${cleaning_prefix}/original"   #从ftp拉取数据在hdfs存放位置
job_path="${staging_prefix}/jobid"  #记录当前从ftp拉取数据的任务id，标识当前任务是否成功
warehouse_prefix="${cleaning_prefix}/warehouse"
output_prefix="${cleaning_prefix}/output"
#本地目录
current_path=$(cd `dirname $0`; pwd)
partner_home="${project_path%%/partner-cleaning*}"
local_path="$partner_home/partner-cleaning"
local_prefix="$local_path/data/ftp/tmp" #从ftp拉取数据存储到当前服务器临时目录，存储到hdfs后删除

mkdir -p $local_prefix

yesDay=1

y=`date +%Y`
m=`date +%m`
d=`date +%d`

##############################生产环境 start##################################

driverMemory=1g
executorMemory=2g
executorCores=6
numExecutors=3
parallelism=30
memoryOverhead=512
#
yesDay=1
#
mysqlUser=zaiou
mysqlPass=zaiou
mysqIp=172.16.81.130
mysqlPrefix=/usr/local/mysql/bin
#
ftpIp="172.16.81.130"
ftpPort="21"
ftpUsername="ftp"
ftpPassword="zaiou@123"
ftp_prefix="/ODSFILE"
#
export JAVA_HOME=/usr/java/jdk1.8.0_211
export PATH=$PATH:$JAVA_HOME/bin


##############################生产环境 end##################################


#生成日志所在日期目录
if [ ! -d "$local_path/logs/$y/$m/$d" ]; then
    mkdir -p "$local_path/logs/$y/$m/$d"
fi
log_path=$local_path/logs/$y/$m/$d



printlnLog(){
    msg=$1
    echo `date "+%Y-%m-%d %H:%M:%S"`" $msg"
}

importDataToMysqlByOriginal(){
    hdfs=$1
    table=$2
    delimiter=$3
    script=$4
    year=$5
    month=$6
    day=$7


    hadoop fs -test -e "$hdfs"
    if [ $? == 0 ]; then
        mkdir -p $local_path/data/$table/$year$month$day
        cd $local_path/data/$table/$year$month$day
        hadoop fs -get $hdfs
        localDir=`pwd`
        filelist=`ls $localDir`
        for file in $filelist ; do

            printlnLog "importDataToMysqlByOriginal $file start"
            $mysqlPrefix/mysql -h$mysqIp -u$mysqlUser -p$mysqlPass   <<EOF
                USE PARTNER;
                LOAD DATA LOCAL INFILE "$local_path/data/$table/$year$month$day/$file"
                REPLACE INTO TABLE  $table
                FIELDS TERMINATED BY "$delimiter"
                ($script);
EOF
            printlnLog "importDataToMysqlByOriginal $file end"
            rm -rf $local_path/data/$table/$year$month$day/$file
        done
    else
        echo "$hdfs 文件不存在,无法更新数据到mysql"
    fi
}
