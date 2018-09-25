package com.zaiou.common.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Description: 身份证自定义验证
 * @auther: LB 2018/9/20 21:25
 * @modify: LB 2018/9/20 21:25
 */
@Documented
@Constraint(validatedBy = {})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Size(min = 18, max = 18)
@Pattern(regexp = "(^(\\d{6})(19|20)(\\d{2})(1[0-2]|0[1-9])(0[1-9]|[1-2][0-9]|3[0-1])(\\d{3})(\\d|X)?$)")
@ReportAsSingleViolation
public @interface IdCardValidate {
    String message() default "0017";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
