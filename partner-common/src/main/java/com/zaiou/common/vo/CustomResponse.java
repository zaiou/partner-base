package com.zaiou.common.vo;

import com.zaiou.common.enums.ResultInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description: 公共返回
 * @auther: LB 2018/10/29 16:41
 * @modify: LB 2018/10/29 16:41
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CustomResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String respCode;
    private String respMsg;
    private Response data;

    public CustomResponse(String code, String message) {
        this.respCode = code;
        this.respMsg = message;
    }

    public CustomResponse(Response data) {
        this.respCode = data.getRespCode();
        this.respMsg = data.getRespMsg();
        this.data = data;
    }

    public CustomResponse(ResultInfo en) {
        this.respCode = en.getCode();
        this.respMsg = en.getCacheMsg();
    }

}
