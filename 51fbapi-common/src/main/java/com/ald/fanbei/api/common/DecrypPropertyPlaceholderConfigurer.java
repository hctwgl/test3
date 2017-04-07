package com.ald.fanbei.api.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.ald.fanbei.api.common.util.AesUtil;


/**
 * 
 *@类DecrypPropertyPlaceholderConfigurer.java 的实现描述：
 *@author 陈金虎 2017年1月16日 下午11:22:59
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class DecrypPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String password;
    
    @Override
    protected String convertProperty(String propertyName, String propertyValue) {
        
        if(encryptPropNames.contains(propertyName)){//属性propertyName的值加密了，需要解密
//        	if(logger.isDebugEnabled()){
        		logger.debug("propertyName=" + propertyName + ",propertyValue=" + propertyValue);
//        	}
            String decryPro = AesUtil.decryptFromBase64(propertyValue, password);
        	if(logger.isDebugEnabled()){
        		logger.debug("decryPro=" + decryPro);
        	}
            return decryPro;
        }
        
        return super.convertProperty(propertyName, propertyValue);
        
    }
    
    private List<String> encryptPropNames;//保存加密的属性字段

    public List<String> getEncryptPropNames() {
        return encryptPropNames;
    }

    public void setEncryptPropNames(List<String> encryptPropNames) {
        this.encryptPropNames = encryptPropNames;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }
    
    
}


