package com.zaiou.web.vo.system;

import com.zaiou.common.annotation.IdCardValidate;
import com.zaiou.common.vo.WebRequest;
import com.zaiou.web.annotation.Log;
import com.zaiou.web.validate.group.SaveValidate;
import com.zaiou.web.validate.group.UpdateValidate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Description: 系统用户vo
 * @auther: LB 2018/9/20 19:51
 * @modify: LB 2018/9/20 19:51
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class SysUserReq extends WebRequest {
    private static final long serialVersionUID = 6828388274867366306L;

    @NotBlank(message = "0001,主键", groups = { UpdateValidate.class })
    private String id;

    @Log(fileName="登录账号")
    @NotBlank(message = "0001,登录账号", groups = { SaveValidate.class, UpdateValidate.class })
    @Length(max = 40, message = "0002,登录账号,40", groups = { SaveValidate.class, UpdateValidate.class })
    private String userCode;

    @Log(fileName="用户名")
    @NotBlank(message = "0001,用户名", groups = { SaveValidate.class, UpdateValidate.class })
    @Length(max = 33, message = "0002,用户名,33", groups = { SaveValidate.class, UpdateValidate.class })
    private String userName;

    @NotBlank(message = "0001,用户密码", groups = { SaveValidate.class, UpdateValidate.class })
    @Length(max = 20, message = "0002,用户密码,33", groups = { SaveValidate.class, UpdateValidate.class })
    private String userPassword;

    @IdCardValidate(message="0005,身份证号格式不正确", groups = { SaveValidate.class, UpdateValidate.class })
    @NotBlank(message = "0001,身份证号", groups = { SaveValidate.class, UpdateValidate.class })
    private String idCard;

    @NotBlank(message = "0001,手机号码")
    @Length(max = 30, message = "0002,手机号码,30")
    private String mobile;

    @NotBlank(message = "0001,用户状态", groups = { SaveValidate.class, UpdateValidate.class })
    private String status;

    @NotBlank(message = "0001,角色")
    @Length(max = 100, message = "0004,角色,100")
    private String roleCode;

    private String roleName;

}
