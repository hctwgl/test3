package com.ald.fanbei.api.biz.foroutapi.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author honghzengpei 2017/10/23 14:48
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AFBRPCService {
    String name();
}
