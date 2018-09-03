package com.ald.fanbei.api.web.common.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.web.common.JsdH5Handle;


/**
 * 
 *@类描述：接口工厂类
 *@author 江荣波 2017年1月17日 上午12:32:03
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("dsedH5HandleFactory")
public class JsdH5HandleFactory {
    @Resource
    Map<String,JsdH5Handle>  h5HandleMap;
  
    public JsdH5Handle getHandle(String method){
        if(StringUtils.isBlank(method)){
            return null;
        }
        return h5HandleMap.get(method);
    }

}
