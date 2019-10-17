#!/bin/bash

echo "**********开始启动集群所有节点服务****************"
echo "**********开始启动zookeeper***********************"
for i in zaiou@hdp1 zaiou@hdp2
do
        ssh $i 'source /etc/profile;/opt/module/zookeeper-3.4.14/bin/zkServer.sh start'
done
echo "************zookeeper启动完成***********************"

echo "************开始启动hdfs****************************"
ssh zaiou@hdp1 '/opt/module/hadoop-2.7.7/sbin/start-dfs.sh'
echo "************dfs启动完成***************************"

echo "************开始启动hdp1 yarn***********************"
ssh zaiou@hdp1 '/opt/module/hadoop-2.7.7/sbin/start-yarn.sh'
echo "************hdp1 yarn启动完成**********************"

echo "************开始启动hdp2 resourcemanager***********"
ssh zaiou@hdp2 '/opt/module/hadoop-2.7.7/sbin/yarn-daemon.sh start resourcemanager'
echo "***********hdp2 resourcemanager 启动完成***********"

echo "**********在hdp1上启动JobHistoryServer*************"
ssh zaiou@hdp1 '/opt/module/hadoop-2.7.7/sbin/mr-jobhistory-daemon.sh start historyserver'
echo "**********hdp1上启动JobHistoryServer完成************"

echo "******** 集群启动完成********************"