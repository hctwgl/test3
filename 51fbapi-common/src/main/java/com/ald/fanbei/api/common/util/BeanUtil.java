package com.ald.fanbei.api.common.util;

import java.beans.PropertyDescriptor;
import java.util.HashMap;

import org.apache.commons.beanutils.PropertyUtilsBean;

/**
 *@类描述：
 *@author 苏伟丽 2016年10月31日 下午7:02:15
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BeanUtil {
	public static HashMap<String, Object> beanToMap(Object obj) {   
        HashMap<String, Object> params = new HashMap<String, Object>(0);   
            try {   
                PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();   
                PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);   
                for (int i = 0; i < descriptors.length; i++) {   
                    String name = descriptors[i].getName();   
                    if (!"class".equals(name)) {   
                        params.put(name, propertyUtilsBean.getNestedProperty(obj, name));   
                    }   
                }   
            } catch (Exception e) {   
                e.printStackTrace();   
            }   
            return params;   
    }  
}
