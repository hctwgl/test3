package com.ald.fanbei.api.biz.service.boluome;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * 
 * @类描述：第三方调用51返呗接口处理类
 * @author xiaotianjian 2017年3月24日下午6:16:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ThirdNotify {

	private static final Logger logger = LoggerFactory.getLogger(ThirdNotify.class);
	
    /**
     * 验证签名
     * @param params
     * @return
     */
    public static boolean verify(Map<String, String> params) {
    	logger.info("verify begin params = {}", params);
	    String sign = params.get("sign");
	    if (StringUtils.isEmpty(sign)) {
	    	return false;
	    }
	    
	    return  getSignVeryfy(params, sign);
    }

    /**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
	private static boolean getSignVeryfy(Map<String, String> Params, String sign) {
		logger.info("getSignVeryfy begin sign = {}", sign);
    	//过滤空值、sign与sign_type参数
    	Map<String, String> sParaNew = BoluomeCore.paraFilter(Params);
        //获取待签名字符串
        String preSignStr = ThirdCore.buildSignStr(sParaNew);
        logger.info("getSignVeryfy begin preSignStr = {}", preSignStr);
        return sign.equals(preSignStr);
    }
}
