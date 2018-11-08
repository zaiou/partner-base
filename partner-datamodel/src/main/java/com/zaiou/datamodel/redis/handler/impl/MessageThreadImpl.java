package com.zaiou.datamodel.redis.handler.impl;

import com.zaiou.common.vo.datamodel.ProcessMessageReq;
import com.zaiou.datamodel.redis.handler.MessageHandler;

/**
 * @Description:消息处理线程
 * @auther: LB 2018/11/1 10:18
 * @modify: LB 2018/11/1 10:18
 */
public class MessageThreadImpl implements Runnable{

    private ProcessMessageReq req;
    private MessageHandler messageHandler;
    private String threadName;
    private int index;

    public MessageThreadImpl(String threadName,MessageHandler messageHandler,ProcessMessageReq req,int index){
        this.req = req;
        this.messageHandler = messageHandler;
        this.threadName = threadName;
        this.index = index;
    }


    @Override
    public void run() {
        Thread.currentThread().setName(threadName);
        // 正常业务处理
        if(index==1) {
            messageHandler.handler(req);
        }
    }
}
