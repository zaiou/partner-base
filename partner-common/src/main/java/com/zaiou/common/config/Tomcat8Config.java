package com.zaiou.common.config;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.coyote.http11.Http11AprProtocol;
import org.apache.coyote.http11.Http11Nio2Protocol;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @auther: LB 2018/9/10 14:39
 * @modify: LB 2018/9/10 14:39
 */
@Configuration
@ConditionalOnProperty(name = "server.tomcat.protocol.enable", havingValue = "true")
public class Tomcat8Config {
    @Value("${server.tomcat.protocol.enable:false}")
    private boolean enableProtocol;
    @Value("${server.tomcat.protocol:'org.apache.coyote.http11.Http11NioProtocol'}")
    private String tomcatProtocol;

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        if (enableProtocol) {
            tomcat.setProtocol(tomcatProtocol);
            tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {
                @Override
                public void customize(Connector connector) {
                    if (tomcatProtocol.contains("Http11AprProtocol")) {
                        Http11AprProtocol protocol = (Http11AprProtocol) connector
                                .getProtocolHandler();
                        // 设置最大连接数
                        protocol.setMaxConnections(2048);
                        // 设置最大线程数
                        protocol.setMaxThreads(1024);// 最大并发连接数
                        protocol.setConnectionTimeout(10000);
                        protocol.setAcceptorThreadCount(100);
                        protocol.setMinSpareThreads(30);
                        protocol.setProcessorCache(-1);
                        // protocol.setSoLinger(0);
                        protocol.setTcpNoDelay(true);
                    }
                    if (tomcatProtocol.contains("Http11NioProtocol")) {
                        Http11NioProtocol protocol = (Http11NioProtocol) connector
                                .getProtocolHandler();
                        // 设置最大连接数
                        protocol.setMaxConnections(2048);
                        // 设置最大线程数
                        protocol.setMaxThreads(1024);// 最大并发连接数
                        protocol.setConnectionTimeout(10000);
                        protocol.setAcceptorThreadCount(100);
                        protocol.setMinSpareThreads(30);
                        protocol.setProcessorCache(-1);
                        // protocol.setSoLinger(0);
                        protocol.setTcpNoDelay(true);
                        protocol.setPollerThreadCount(40 * 1024);
                    }
                    if (tomcatProtocol.contains("Http11Nio2Protocol")) {
                        Http11Nio2Protocol protocol = (Http11Nio2Protocol) connector
                                .getProtocolHandler();
                        // 设置最大连接数
                        protocol.setMaxConnections(2048);
                        // 设置最大线程数
                        protocol.setMaxThreads(1024);// 最大并发连接数
                        protocol.setConnectionTimeout(10000);
                        protocol.setAcceptorThreadCount(100);
                        protocol.setMinSpareThreads(30);
                        protocol.setProcessorCache(-1);
                        // protocol.setSoLinger(0);
                        protocol.setTcpNoDelay(true);
                    }
                }
            });
            if (tomcatProtocol.contains("Http11AprProtocol")) {
                tomcat.addContextLifecycleListeners(new AprLifecycleListener());
            }
        }
        return tomcat;
    }
}
