package com.ald.fanbei.api.biz.third.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CollectionDataBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.common.enums.AfRepayCollectionType;
import com.ald.fanbei.api.common.enums.AfRepeatCollectionType;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：和资产方系统调用工具类
 * @author chengkang 2017年11月29日 16:55:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("assetSideEdspayUtil")
public class AssetSideEdspayUtil extends AbstractThird {

	@Resource
    AfResourceService afResourceService;
	
	public CollectionOperatorNotifyRespBo offlineRepaymentNotify(String timestamp, String data, String sign) {
		// 响应数据,默认成功
		CollectionOperatorNotifyRespBo notifyRespBo = new CollectionOperatorNotifyRespBo(FanbeiThirdRespCode.SUCCESS);
		try {
			CollectionOperatorNotifyReqBo reqBo = new CollectionOperatorNotifyReqBo();
			reqBo.setData(data);
			reqBo.setSign(DigestUtil.MD5(data));
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

				// 参数校验
				if (StringUtil.isAllNotEmpty(repayNo, borrowNo, repayType, repayTime, repayAmount, restAmount, tradeNo, isBalance)) {
					// 业务处理
					String respCode = null;
					//afRepaymentBorrowCashService.dealOfflineRepaymentSucess(repayNo, borrowNo, repayType, repayTime, NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO), NumberUtil.objToBigDecimalDivideOnehundredDefault(restAmount, BigDecimal.ZERO), tradeNo, isBalance);
					FanbeiThirdRespCode respInfo = FanbeiThirdRespCode.findByCode(respCode);
					notifyRespBo.resetMsgInfo(respInfo);
				} else {
					notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST);
				}
			} else {
				notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR);
			}
		} catch (Exception e) {
			logger.error("offlineRepaymentNotify error", e);
			notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.SYSTEM_ERROR);
		}
		String resDataJson = JsonUtil.toJSONString(notifyRespBo);
		notifyRespBo.setSign(DigestUtil.MD5(resDataJson));
		return notifyRespBo;
	}
}
