package com.ald.fanbei.web.common.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.common.exception.FanbeiException;
import com.ald.fanbei.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.web.common.ApiHandle;



/**
 * 
 *@类描述：接口工厂类
 *@author 陈金虎 2017年1月17日 上午12:32:03
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("apiHandleFactory")
public class ApiHandleFactory {
    @Resource
    Map<String,ApiHandle>  apiHandleMap;
    @Resource
    List<String> beforeLoginApiList ;

    public ApiHandle getApiHandle(String method){
        if(StringUtils.isBlank(method)){
            return null;
        }
        return apiHandleMap.get(method);
    }
    
    /**
     * 验证该方法是否是登录之前的方法 
     * @param method
     * @return true:登录之前的方法  false:登录之后的方法
     */
    public boolean checkBeforlogin(String method){
        if(apiHandleMap.get(method) == null){
            throw new FanbeiException("request method not exist", FanbeiExceptionCode.REQUEST_PARAM_METHOD_NOT_EXIST);
        }
        if(beforeLoginApiList != null && beforeLoginApiList.contains(method)){
            return true;
        }
        return false;
    }
    
    
    
}
