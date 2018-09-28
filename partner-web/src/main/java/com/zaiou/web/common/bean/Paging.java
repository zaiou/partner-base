package com.zaiou.web.common.bean;

import lombok.Data;
import org.apache.ibatis.session.RowBounds;

/**
 * @Description: 与具体ORM实现无关的分页参数及查询结果封装. 注意所有序号从1开始.
 * @auther: LB 2018/9/25 17:07
 * @modify: LB 2018/9/25 17:07
 */
@Data
public class Paging extends RowBounds implements IPage {
    // -- 分页参数 --//
    protected int pageNumber = 1;
    protected int pageSize = 10;
    protected long totalCount = 0;
    protected int pageCount = 0;
}
