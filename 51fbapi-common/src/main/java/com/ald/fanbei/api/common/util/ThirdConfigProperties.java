package com.ald.fanbei.api.common.util;


import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import com.ald.fanbei.api.common.Constants;

/**
 * 
 * @类描述：第三方APPkey配置
 * @author xiaotianjian 2017年3月28日下午11:44:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ThirdConfigProperties {

    private static Properties config     = new Properties();

    private String            configPath = null;

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void init() {
        try {
        	if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_ONLINE)) {
        		configPath = "props/appKey_online.properties";
        	} else {
        		configPath = "props/appKey_test.properties";
        	}
        	
            config.load(ThirdConfigProperties.class.getClassLoader().getResourceAsStream(configPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return
     */
    public static Properties getConfig() {
        return config;
    }

    /**
     * Gets the.
     * 
     * @param key the key
     * @return the string
     */
    public static String get(String key) {
        return config.getProperty(key);
    }

    /**
     * Gets the.
     * 
     * @param key the key
     * @param defaultValue the default value
     * @return the string
     */
    public static String get(String key, String defaultValue) {
        return config.getProperty(key, defaultValue);
    }
    
    public static void main(String[] args) {
		System.out.println(UUID.randomUUID());
	}
}
