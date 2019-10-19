#!/bin/sh

## 获取util脚本
project_path=$(cd `dirname $0`; pwd)
source "${project_path%%script*}/script/util/util.sh"

year=`date +%Y`
month=`date +%m`
day=`date +%d -d "-$yesDay days"`
hour=`date +%H`

yearSocial=`date +%Y`
monthSocial=`date +%m`
daySocial=`date +%d`
hourSocial=`date +%H`

sys_name=$1
business_name=$2

hadoop fs -test -e "$staging_prefix/ftp"
if [ $? != 0 ]; then
    hadoop fs -mkdir -p "$staging_prefix/ftp"
fi

hadoop fs -test -e "$original_prefix/ftp"
if [ $? != 0 ]; then
    hadoop fs -mkdir -p "$original_prefix/ftp"
fi

hadoop fs -test -e "$staging_prefix/JobId"
if [ $? != 0 ]; then
    hadoop fs -mkdir -p "$staging_prefix/JobId"
fi

if [ $sys_name = "xd" ]; then
    if [ $business_name = "ftp" ]; then
        java -Djava.ext.dirs=$local_path/lib/run -cp $local_path/partner-cleaning-0.0.1-SNAPSHOT.jar com.zaiou.cleaning.lock.Main $local_path/script/shell/xd.sh $year $month $day $hour $sys_name $business_name >> $log_path/ftpToHdfs_xd_ftp.log 2>&1
    fi
    if [ $business_name = "parse" ]; then
        java -Djava.ext.dirs=$local_path/lib/run -cp $local_path/partner-cleaning-0.0.1-SNAPSHOT.jar com.zaiou.cleaning.lock.Main $local_path/script/shell/xd.sh $year $month $day $hour $sys_name $business_name >> $log_path/ftpToHdfs_xd_parse.log 2>&1
    fi
fi
