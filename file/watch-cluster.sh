#!/bin/bash
echo  "************* 开始启动JPS  **********"
echo  "************* hdp1的jps **********"
ssh   zaiou@hdp1  'source /etc/profile;jps'
echo  "************* hdp2的jps **********"
ssh   zaiou@hdp2  'source /etc/profile;jps'