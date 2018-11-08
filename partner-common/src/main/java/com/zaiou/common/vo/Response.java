package com.zaiou.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zaiou.common.enums.ResultInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @Description: 返回
 * @auther: LB 2018/10/29 16:43
 * @modify: LB 2018/10/29 16:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Scope("prototype")
@Primary
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    protected String respCode;
    @JsonIgnore
    protected String respMsg;

    public Response(ResultInfo e) {
        setResult(e);
    }

    @JsonIgnore
    public void setResult(ResultInfo e) {
        respCode = e.getCode();
        respMsg = e.getCacheMsg();
    }
}
