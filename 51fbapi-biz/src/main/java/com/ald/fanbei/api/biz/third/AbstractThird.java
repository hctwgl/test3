package com.ald.fanbei.api.biz.third;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.common.util.StringUtil;

/**
 * 
 *@类描述：第三方抽象类
 *@author 陈金虎 2017年1月17日 上午12:07:21
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class AbstractThird {
    protected static final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");
    protected static Logger   logger           = LoggerFactory.getLogger(AbstractThird.class);
    
    /**
     * 打印第三方日志
     * @param resp 第三方响应结果
     * @param methodName 接口名称
     * @param param 参数数组
     */
	protected static void logThird(Object resp,String methodName, Object... param) {
		StringBuffer sb = new StringBuffer();
		for (Object item : param) {
			sb = sb.append("|").append(item);
		}
		thirdLog.info(StringUtil.appendStrs("methodName=", methodName,";params=", sb.toString() , ";resp=" , resp==null?"":resp));
	}
}
