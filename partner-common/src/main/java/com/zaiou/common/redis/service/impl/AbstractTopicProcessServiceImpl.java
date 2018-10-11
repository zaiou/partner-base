package com.zaiou.common.redis.service.impl;

import com.google.gson.Gson;
import com.zaiou.common.redis.service.TopicProcessService;
import com.zaiou.common.utils.DesensitizedUtils;
import com.zaiou.common.vo.TopicObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

/**
 * @Description: MQ服务实现抽象类，所有MQ服务都要继承这个类
 * @auther: LB 2018/10/10 11:28
 * @modify: LB 2018/10/10 11:28
 */
@Component
@Slf4j
public abstract class AbstractTopicProcessServiceImpl extends MessageListenerAdapter implements TopicProcessService {

    protected abstract void processMessage(TopicObj msg);

    @Autowired
    private Gson gson;

    @Override
    public void receivedMessage(String msg) {
        TopicObj topic = gson.fromJson(msg, convertCls());
        log.debug("Topic Process.....cls:{} msg:{}", convertCls().getName(), DesensitizedUtils.toJsonString(topic));
        processMessage(topic);
    }

    @Override
    protected String getDefaultListenerMethod() {
        return "receivedMessage";
    }

    protected abstract Class<? extends TopicObj> convertCls();

}
