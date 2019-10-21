#!/bin/sh
source /data/partner/partner-cleaning/script/util/util.sh

t=$1
hdfs_staging_prefix=$2
hdfs_original_prefix=$3
year=$4
month=$5
day=$6
hour=${7}
db_script=$8
t_prefix=$9
sys_name=${10}
total_sys_name=${11}

printlnLog "=====$t======processTable.sh start==hdfs_staging_prefix:$hdfs_staging_prefix==hdfs_original_prefix:$hdfs_original_prefix==year:$year==month:$month==day:$day==total_sys_name:$total_sys_name==sys_name:$sys_name==db_script:$db_script=="

#ftp服务器文件存储目录
ftpFilePath=$ftp_prefix/$total_sys_name/PUB

#根据表全量还是增量生成对应文件名
if [ ${db_script:0:1} = "Y" -o ${db_script:0:1} = "M" ]; then
    okFile="ODS."$total_sys_name"_TMP_"$t"_1208_TXT_"$year$month$day".ok"
    printlnLog "增量表OK文件【$okFile】"
    dataFile="ODS."$total_sys_name"_TMP_"$t"_1208_TXT.txt"
    printlnLog "增量dataFile文件【$dataFile】"
else
    okFile="ODS."$total_sys_name"_MIR_"$t"_1208_TXT_"$year$day".ok"
    printlnLog "全量表OK文件【$okFile】"
    dataFile="ODS."$total_sys_name"_MIR_"$t"_1208_TXT.txt"
    printlnLog "全量表dataFile文件【$dataFile】"
fi

#生成hdfs OK文件目录 --- 在hdfs上存放从ftp拉取的ok文件，标识当天的数据是否从ftp更新
hdfs_ok_path="$hdfs_staging_prefix/ok/$year$month$day"
hadoop fs -test -e $hdfs_ok_path
if [ $? != 0 ]; then
    hadoop fs -mkdir -p $hdfs_ok_path
fi

hadoop fs -test -e $hdfs_ok_path/$okFile
if [ $? = 0 ]; then
    printlnLog "当日文件已完成更新,无需重复更新-->"$hdfs_ok_path/$okFile
else
    #向数据库中插入状态 下载中
    appname="cleaningDown"$t"Job"
    printlnLog "向hdfs中插入状态下载中【appname】:$appname"
    hadoop fs -test -e $job_path/$appname/*
    if [ $? != 0 ]; then
        java -Djava.ext.dirs=$local_path/lib/run/ -cp $local_path/partner-cleaning-0.0.1-SNAPSHOT.jar com.zaiou.cleaning.mapper.InsertSysScheduleLog "$appname"
    fi
    #获取当前任务id
    id=`hadoop fs -ls $job_path/$appname | awk -F "/" 'NR==2{print $NF}'`
    sh $local_path/script/shell/common/getFtpService.sh $ftpFilePath $local_prefix $okFile
    # 判断当前本地文件夹是否有ok文件，有的话说明今日文件已更新，可清洗
    if [ -f "$local_prefix/$okFile" ]; then
        printlnLog $okFile" 文件存在 getFtpService=>$dataFile"
        sh $local_path/script/shell/common/getFtpService.sh $ftpFilePath $local_prefix $dataFile
        
        #创建存储当日清洗文件的hdfs文件夹
        hadoop fs -test -e $hdfs_original_prefix/$t/$year/$month/$day
        if [ $? != 0 ]; then
            hadoop fs -mkdir -p $hdfs_original_prefix/$t/$year/$month/$day
        fi
        
        #如果当日清洗表文件已存在(更新失败的时候会存在此文件),删除文件,重新清洗
        hadoop fs -test -e $hdfs_original_prefix/$t/$year/$month/$day/$dataFile
        if [ $? = 0 ]; then
            hadoop fs -rm $hdfs_original_prefix/$t/$year/$month/$day/$dataFile
        fi

        hadoop fs -put $local_prefix/$dataFile $hdfs_original_prefix/$t/$year/$month/$day

        hadoop fs -test -e $hdfs_original_prefix/$t/$year/$month/$day/$dataFile
        if [ $? = 0 ]; then
            #上传最新的.ok标志到hdfs
            hadoop fs -put $local_prefix/$okFile $hdfs_ok_path/$okFile
            printlnLog "下载成功 【$t】执行更新该表下载状态为1 jobid=$id"
            #更新数据库标示，标示下载成功
            java -Djava.ext.dirs=$local_path/lib/run/ -cp $local_path/partner-cleaning-0.0.1-SNAPSHOT.jar com.zaiou.cleaning.mapper.UpdateSysScheduleLog "$id" "1"
            #删除hdfs中下载进度标示
            hadoop fs -rmr $job_path/$appname/*
        fi

        #删除本地清洗文件
        rm -rf $local_prefix/$okFile
        rm -rf $local_prefix/$dataFile
    else
        #获取当前任务id
        id=`hadoop fs -ls $job_path/$appname | awk -F "/" 'NR==2{print $NF}'`
        printlnLog $okFile " 文件不存在"
        printlnLog "下载失败【$t】更新该表下载状态为0 jobId=$id"
        #更新数据库标示，标示下载失败
        java -Djava.ext.dirs=$local_path/lib/run/ -cp $local_path/partner-cleaning-0.0.1-SNAPSHOT.jar com.zaiou.cleaning.mapper.UpdateSysScheduleLog "$id" "0"
    fi
fi
printlnLog "====$t====processTable.sh end============year:$year===$month:$month===day:$day===total_sys_name:$total_sys_name=====sys_name:$sys_name==="




