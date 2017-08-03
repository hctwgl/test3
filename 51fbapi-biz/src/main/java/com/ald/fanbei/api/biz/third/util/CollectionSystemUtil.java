package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：和催收系统互调工具类
 * @author chengkang 2017年8月3日 16:55:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("collectionSystemUtil")
public class CollectionSystemUtil extends AbstractThird {
	private static String url = null;

	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	
	@SuppressWarnings("unused")
	private static String getUrl() {
		if (url == null) {
			url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL);
			return url;
		}
		return url;
	}
	
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			prestr = prestr + value;
		}
		return prestr;
	}
	
	public CollectionOperatorNotifyRespBo offlineRepaymentNotify(String timestamp, String data, String sign) {
		//响应数据,默认成功
		CollectionOperatorNotifyRespBo notifyRespBo = new CollectionOperatorNotifyRespBo(FanbeiThirdRespCode.SUCCESS); 
		try {
			CollectionOperatorNotifyReqBo reqBo = new CollectionOperatorNotifyReqBo();
			reqBo.setData(data);
			reqBo.setSign(DigestUtil.MD5(createLinkString(reqBo)));
			logThird(sign, "offlineRepaymentNotify", reqBo);
			if (StringUtil.equals(sign, reqBo.getSign())) {// 验签成功
				JSONObject obj = JSON.parseObject(data);
				String repayNo = obj.getString("repay_no");
				String borrowNo = obj.getString("borrow_no");
				String repayType = obj.getString("repay_type");
				String repayTime = obj.getString("repay_time");
				String repayAmount = obj.getString("repay_amount");
				String restAmount = obj.getString("rest_amount");
				String tradeNo = obj.getString("trade_no");
				String isBalance = obj.getString("is_balance");
				
				//参数校验
				if(StringUtil.isAllNotEmpty(repayNo,borrowNo,repayType,repayTime,repayAmount,restAmount,tradeNo,isBalance)){
					//业务处理
					String respCode = afRepaymentBorrowCashService.dealOfflineRepaymentSucess(repayNo, borrowNo, repayType, repayTime, NumberUtil.objToBigDecimalDefault(repayAmount, BigDecimal.ZERO), NumberUtil.objToBigDecimalDefault(restAmount, BigDecimal.ZERO), tradeNo, isBalance);
					FanbeiThirdRespCode respInfo = FanbeiThirdRespCode.findByCode(respCode);
					notifyRespBo.resetMsgInfo(respInfo);
				}else{
					notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST);
				}
			}else{
				notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR);
			}
		} catch (Exception e) {
			logger.error("offlineRepaymentNotify error",e);
			notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.SYSTEM_ERROR);
		}
		notifyRespBo.setSign(DigestUtil.MD5(createLinkString(notifyRespBo)));
		return notifyRespBo;
	}
}
