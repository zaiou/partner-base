#!/bin/bash
echo  "*************      开在关闭集群所有节点服务      *************"
echo  "*************  正在hdp1上关闭JobHistoryServer  *************"
ssh   zaiou@hdp1 '/opt/module/hadoop-2.7.7/sbin/mr-jobhistory-daemon.sh stop historyserver'
echo  "*************         正在关闭YARN               *************"
ssh   zaiou@hdp1 '/opt/module/hadoop-2.7.7/sbin/stop-yarn.sh'
echo "**************         正在关闭hdp2 resourcemanager  **********"
ssh   zaiou@hdp2  '/opt/module/hadoop-2.7.7/sbin/yarn-daemon.sh stop resourcemanager'
echo  "*************         正在关闭HDFS               *************"
ssh   zaiou@hdp1 '/opt/module/hadoop-2.7.7/sbin/stop-dfs.sh'
echo  "*************         正在关闭zookeeper          *************"
for i in zaiou@hdp1 zaiou@hdp2
do
     ssh $i '/opt/module/zookeeper-3.4.14/bin/zkServer.sh stop'
done