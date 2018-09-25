package com.zaiou.web.vo.system;

import com.zaiou.common.vo.BaseVo;
import lombok.Data;

/**
 * @Description: web端基础vo
 * @auther: LB 2018/9/20 20:50
 * @modify: LB 2018/9/20 20:50
 */
@Data
public class WebVo extends BaseVo {
    private static final long serialVersionUID = 2778556716260538765L;

    private int pageNumber = 1;

    private int pageSize = 10;

    private Boolean isPage;
}
