package com.ald.fanbei.api.biz.service.boluome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * 
 * @类描述：菠萝觅处理类
 * @author xiaotianjian 2017年3月24日下午6:16:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomeNotify extends AbstractThird {

	private static final Logger thirdLog = LoggerFactory.getLogger(BoluomeNotify.class);

	/**
	 * 验证签名
	 * 
	 * @param params
	 * @return
	 */
	public static boolean verify(Map<String, String> params) {
		thirdLog.info("verify begin params = {}", params);
		// mqp: free of sign if the environment is local or test
//		String url = params.get("detailUrl");
//		String tem = getEnv(url);
//		if (StringUtil.isAllNotEmpty(tem) && tem.equals("dev-91ala.otosaas.com")) {
//			return true;
//		}
		
		// free of sign if the environment is local or test
		 String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		 if (Constants.INVELOMENT_TYPE_TEST.equals(type) || Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ) {
		     return true;
		 }
		
		String sign = params.get("sign");
		if (StringUtils.isEmpty(sign)) {
			return false;
		}

		return getSignVeryfy(params, sign);
	}

//	private static  String getEnv(String partlyUrl) {
//		String result ="";
//		if (StringUtil.isNotEmpty(partlyUrl)) {
//			String[] arrayStr = partlyUrl.split("/");
//			result = arrayStr[2];
//		}
//		return result;
//	}

	/**
	 * 根据反馈回来的信息，生成签名结果
	 * 
	 * @param Params
	 *            通知返回来的参数数组
	 * @param sign
	 *            比对的签名结果
	 * @return 生成的签名结果
	 */
	private static boolean getSignVeryfy(Map<String, String> Params, String sign) {
		thirdLog.info("getSignVeryfy begin sign = {}", sign);
		// 过滤空值、sign与sign_type参数
		Map<String, String> sParaNew = BoluomeCore.paraFilter(Params);
		// 获取待签名字符串
		String preSignStr = BoluomeCore.buildSignStr(sParaNew);
		thirdLog.info("getSignVeryfy begin preSignStr = {}", preSignStr);
		return sign.equals(preSignStr);
	}
}
