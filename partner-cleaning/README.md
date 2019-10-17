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
e：hbase  http://hdp1:16010
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
zookeeper   root
spark       zaiou
hbase       zaiou

注意：zookeeper需要root用户启动，否则客户端访问不了


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

10、java.lang.NoClassDefFoundError: org/apache/hadoop/hbase/HBaseConfiguration
a、（hadoop jar）hadoop应用缺少hbase依赖;不能用项目lib文件夹下面的jar包，删除项目lib下面关于hbase的jar，需要用hbase lib下面完整的jar
https://blog.csdn.net/mtj66/article/details/52366336
https://chengjianxiaoxue.iteye.com/blog/2169688

b、spark-submit 提交的时候报错 把hbase lib文件夹下面的依赖包拷贝到 spark jars文件夹下
https://blog.csdn.net/Hiwes/article/details/80228590 


11、项目运行优先选择项目lib下的依赖jar，然后选择服务器环境中的依赖

12、mvn install会执行junit测试函数，所以单元测试执行完需要注释掉@Test

13、HDFS运行在伪分布式模式（local）下，defaultFS=hdfs://localhost:9000 Hadoop配置在项目中不会加载，需要在项目中的每个hdfs目录前
    加上 hdfs://localhost:9000,才会读取hdfs文件，否则读取本地文件；
    或者拷贝hdfs目录下配置文件 core-site.xml, hdfs-site.xml 到$PROJECT_DIR/src/main/resources目录下；
    在hbase使用情况下一定要把hdfs目录下配置文件 core-site.xml, hdfs-site.xml 到$PROJECT_DIR/src/main/resources目录下
    参考：https://blog.csdn.net/adorechen/article/details/78942536

ftp文件为gz压缩文件怎么处理

