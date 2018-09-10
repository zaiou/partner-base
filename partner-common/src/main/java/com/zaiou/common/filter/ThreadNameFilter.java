package com.zaiou.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Description: 线程名过滤器
 * @auther: LB 2018/9/8 16:09
 * @modify: LB 2018/9/8 16:09
 */
@Slf4j
public class ThreadNameFilter extends OncePerRequestFilter {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String name = Thread.currentThread().getName();
        Thread.currentThread().setName(getOnlyPK());
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        log.info("当前的线程名称------------>"+name);
        Thread.currentThread().setName(name);
    }

    public static String getOnlyPK() {
        int min = 1000000;
        int max = 9999999;
        int tmp = new Random().nextInt(Integer.MAX_VALUE);
        StringBuilder sb = new StringBuilder();
        sb.append(simpleDateFormat.format(new Date()));
        sb.append(tmp % (max - min + 1) + min);
        return sb.toString();
    }
}
