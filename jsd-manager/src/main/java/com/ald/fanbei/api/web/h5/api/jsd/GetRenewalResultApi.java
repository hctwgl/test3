package com.ald.fanbei.api.web.h5.api.jsd;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.enums.JsdRenewalDetailStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;


/**  
 * @Description: 极速贷 获取续期结果
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年8月30日
 */
@Component("getRenewalResultApi")
public class GetRenewalResultApi implements JsdH5Handle {

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    private JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    private JsdBorrowCashService jsdBorrowCashService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private JsdResourceService jsdResourceService;
    @Resource
    private JsdBorrowCashRenewalService jsdBorrowCashRenewalService;

	@Override
	public JsdH5HandleResponse process(Context context) {
		
		String delayNo = ObjectUtils.toString(context.getDataMap().get("delayNo"), ""); // 续期编号
		String timestamp = ObjectUtils.toString(context.getDataMap().get("timestamp"), ""); // 请求时间戳
		
		JsdBorrowCashRenewalDo renewalDo = jsdBorrowCashRenewalService.getByTradeNoXgxy(delayNo);
		if(renewalDo == null) throw new BizException(BizExceptionCode.RENEWAL_NOT_EXIST_ERROR);
		
		String status = renewalDo.getStatus();
		String tradeNo = renewalDo.getTradeNo();
		String failMsg = renewalDo.getFailMsg();
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("status", status);
		data.put("tradeNo", tradeNo);
		if(JsdRenewalDetailStatus.NO.getCode().equals(status)){
			data.put("timestamp", failMsg);
		}
		data.put("timestamp", timestamp);
		
		JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功", data);

		return resp;
	}
}

