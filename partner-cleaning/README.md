项目部署问题记录：
1、项目所在文件设置为zaiou用户所属文件
```
chown -R  zaiou:zaiou /data/partner/
```
2、设置hdfs中/user文件为zaiou所属文件
```
su hdfs
hadoop fs -chown zaiou:zaiou /user
```
3、sh main.sh xd ftp 执行的时候少了参数ftp ，当脚本中if条件判断的时候会报错： 
第 33 行:[: =: 期待一元表达式

4、各个项目访问地址
a: cloudera： http://172.16.81.128:7180
b:
    cdh的环境下，hdfs是8020端口，conf.set(“fs.defaultFS”, “hdfs://hdp1:8020”); 
    普通hadoop环境，hdfs是9000端口，conf.set(“fs.defaultFS”, “hdfs://hdp1:9000”)
c:yarn   http://172.16.81.130:8088/
d:spark  http://172.16.81.130:8080/
5、项目启动脚本
start-dfs.sh
start-yarn.sh
zkServer.sh start
systemctl start vsftpd.service
sh /opt/module/spark-2.4.3/sbin/start-all.sh
start-hbase.sh
6、shell语法中不识别的字段直接当作字符串，mysql语法中不识别的字段不能当作字符串，会报错，需要在字段上加引号；
shell中相互传字段后字符串的引号接受参数后会去掉
7、idea中创建scala文件需要在当前项目中 Libraries中下载scala-jdk并作为当前项目依赖
8、hadoop项目需要安装：
hadoop集群  zaiou
mysql       root
scala       zaiou
zookeeper   zaiou
spark       zaiou
hbase       zaiou

9、[YARN] 2.2 GB of 2.1 GB virtual memory used. Killing container.
spark程序在yarn的集群运行，出现 Current usage: 105.9 MB of 1 GB physical memory used;
 2.2 GB of 2.1 GB virtual memory used. Killing container. 错误。
我的运行环境是虚拟机，每个虚拟机分配1G的物理内存。但是这个错误跟物理内存无关，是虚拟内存超了。
解决方法：
在etc/hadoop/yarn-site.xml文件中，修改检查虚拟内存的属性为false，如下：
``
<property>
    <name>yarn.nodemanager.vmem-check-enabled</name>
    <value>false</value>
</property>
``

ftp文件为gz压缩文件怎么处理

