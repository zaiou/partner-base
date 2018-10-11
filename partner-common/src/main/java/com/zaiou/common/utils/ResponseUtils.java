package com.zaiou.common.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Description: 返回给前端json数据
 * @auther: LB 2018/10/9 11:55
 * @modify: LB 2018/10/9 11:55
 */
public class ResponseUtils {
    /**
     * 发送json。使用UTF-8编码。
     *
     * @param response
     *            HttpServletResponse
     * @param text
     *            发送的字符串
     */
    public static void renderJson(HttpServletResponse response, String text) {
        render(response, "application/json;charset=UTF-8", text);
    }

    /**
     * 发送xml。使用UTF-8编码。
     *
     * @param response
     *            HttpServletResponse
     * @param text
     *            发送的字符串
     */
    public static void renderXml(HttpServletResponse response, String text) {
        render(response, "text/xml;charset=UTF-8", text);
    }

    /**
     * 发送内容。使用UTF-8编码。
     *
     * @param response
     * @param contentType
     * @param text
     */
    public static void render(HttpServletResponse response, String contentType, String text) {
        response.setContentType(contentType);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        PrintWriter out = null;
        try {
            // System.out.println(">>>responseText:" + responseText);
            out = response.getWriter();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }

        out.print(text);
        out.flush();
    }
}
