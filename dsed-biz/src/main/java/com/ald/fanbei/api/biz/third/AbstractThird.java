package com.ald.fanbei.api.biz.third;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;

/**
 * 
 * @类描述：第三方抽象类
 * @author 陈金虎 2017年1月17日 上午12:07:21
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class AbstractThird {
	protected static final Logger thirdLog = LoggerFactory.getLogger("DSED_THIRD");
	protected static final Logger maidianLog = LoggerFactory.getLogger("DSED_MD");
	protected Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 打印第三方日志
	 * 
	 * @param resp
	 *            第三方响应结果
	 * @param methodName
	 *            接口名称
	 * @param param
	 *            参数数组
	 */
	protected void logThird(Object resp, String methodName, Object... param) {
		StringBuffer sb = new StringBuffer();
		for (Object item : param) {
			sb = sb.append("|").append(JSON.toJSONString(item));
		}
		logger.info(StringUtil.appendStrs("methodName=", methodName, ";params=", sb.toString().replaceAll("\n", ""), ";resp=", resp == null ? "" : resp));
	}
}
