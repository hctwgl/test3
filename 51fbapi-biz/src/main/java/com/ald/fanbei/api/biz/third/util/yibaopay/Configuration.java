package com.ald.fanbei.api.biz.third.util.yibaopay;

import java.util.ResourceBundle;

/**
 * @author honghzengpei 2017/9/8 11:05
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Configuration {
    private static Object lock              = new Object();
    private static Configuration config     = null;
    private static ResourceBundle rb        = null;
    private static final String CONFIG_FILE = "merchantInfo";

    private Configuration() {
        rb = ResourceBundle.getBundle(CONFIG_FILE);
    }

    public static Configuration getInstance() {
        synchronized(lock) {
            if(null == config) {
                config = new Configuration();
            }
        }
        return (config);
    }

    public String getValue(String key) {
        return (rb.getString(key));
    }
}
