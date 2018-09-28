package com.zaiou.web.common.bean;

import com.github.pagehelper.PageInfo;
import com.zaiou.common.enums.ResultInfo;

/**
 * @Description: 返回给前端的字段
 * @auther: LB 2018/9/25 17:06
 * @modify: LB 2018/9/25 17:06
 */
public class RespBody {

    // 响应码 0000为成功，否则失败
    private String code;
    // 响应信息
    private String message;
    // 数据对象
    private Object data;
    // 显示到前端对象
    private Paging page;

    public RespBody() {
        this.code = ResultInfo.SUCCESS.getCode();
        this.message = ResultInfo.SUCCESS.getCacheMsg();
    }

    public RespBody(ResultInfo resultInfo) {
        this.code = resultInfo.getCode();
        this.message = resultInfo.getCacheMsg();
    }

    public RespBody(ResultInfo resultInfo, Object data) {
        this.code = resultInfo.getCode();
        this.message = resultInfo.getCacheMsg();
        this.data = data;
    }

    public RespBody(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static RespBody result(Object data) {
        return new RespBody(ResultInfo.SUCCESS, data);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Paging getPage() {
        return page;
    }

    public void setPage(PageInfo<?> pageInfo) {
        if (null != pageInfo) {
            page = new Paging();
            if(0 == pageInfo.getPageNum()){
                page.setPageNumber(1);
            }else{
                page.setPageNumber(pageInfo.getPageNum());
            }
            page.setPageSize(pageInfo.getPageSize());
            page.setTotalCount(pageInfo.getTotal());
            page.setPageCount(pageInfo.getPages());
        }
    }

}
