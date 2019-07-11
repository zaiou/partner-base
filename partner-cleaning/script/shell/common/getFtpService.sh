#!/bin/sh
source /data/partner/partner-cleaning/script/util/util.sh

ftp_prefix=$1
local_prefix=$2
file=$3

echo "-- ftp下载文件： getFtpService[$ftp_prefix/$file]-->[$local_prefix] start"

/usr/bin/ftp -n $ftpIp $ftpPort << END_SCRIPT
user $ftpUsername $ftpPassword
cd $ftp_prefix
lcd $local_prefix
passive
prompt
bin
get $file
close
bye
!
END_SCRIPT

echo "-- ftp下载文件： getFtpService[$ftp_prefix/$file]-->[$local_prefix] end"
