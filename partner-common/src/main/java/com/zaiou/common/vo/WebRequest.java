package com.zaiou.common.vo;

import lombok.Data;

/**
 * @Description: web端基础vo
 * @auther: LB 2018/9/20 20:50
 * @modify: LB 2018/9/20 20:50
 */
@Data
public class WebRequest extends Request {
    private static final long serialVersionUID = 2778556716260538765L;

    private int pageNumber = 1;

    private int pageSize = 10;

    private Boolean isPage;
}
