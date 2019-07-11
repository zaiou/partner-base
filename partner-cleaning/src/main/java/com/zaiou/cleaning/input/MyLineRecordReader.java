package com.zaiou.cleaning.input;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

/**
 * @author zaiou 2019-06-14
 * @Description:
 * @modify zaiou 2019-06-14
 */
@InterfaceAudience.LimitedPrivate({"MapReduce", "Pig"})
@InterfaceStability.Evolving
public class MyLineRecordReader extends RecordReader<LongWritable, Text> {
    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {

    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        return false;
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {
        return null;
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return null;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }

    private static final byte START_STR = '"';
    private static final byte END_STR = '"';

    /**
     * 预处理双引号中间的逗号
     * @param oldStr
     * @return
     */
    public static String preProcess(String oldStr) {
        byte[] buffer = oldStr.getBytes();
        boolean stringStart = false;   //字符串 开始标志  【true 已开始，false 未开始】
        boolean stringEnd = true;  //字符串 结束标志  【true 已结束，false 未结束】
        for(int i=0;i<buffer.length;i++){
            if (buffer[i] == START_STR && stringEnd && !stringStart) {  //如果当前字节等于双引号，并且结束标志是true[已结束],并且开始标志是false[未开始]  那么开始标志设置已开始,结束标志设置未结束
                if(i > 0 && buffer[i-1] == ','){   //不是  数组开始0下标   判断前一位是不是逗号
                    stringStart = true;  //设置开始标志已开始
                    stringEnd = false;  //设置结束标志未结束  stringEnd结束之前，中间所有的换行都是无效的
                }
            }else{
                if (buffer[i] == END_STR && !stringEnd && stringStart) {  //如果当前字节等于双引号，并且结束标志是false[未结束]，并且开始标志是true【已开始】 那么开始标志设置未开始，结束标志设置已结束
                    if(buffer.length > i+1 && buffer[i+1] == ','){  //不是  数组 末尾   判断后一位  是不是逗号
                        stringStart = false;  //设置开始标志未开始
                        stringEnd = true;   //设置结束标志已结束
                    }
                }
            }
            if (buffer[i] == ',' && !stringEnd && stringStart) {  //如果当前字节等于逗号，并且结束标志是false[未结束]，并且开始标志是true【已开始】 那么替换中间的逗号为|
                buffer[i] = '|';
            }
        }
        return new String(buffer);
    }
}
