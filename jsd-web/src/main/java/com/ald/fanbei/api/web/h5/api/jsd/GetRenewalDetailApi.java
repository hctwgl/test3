package com.ald.fanbei.api.web.h5.api.jsd;

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
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.google.common.collect.Maps;


/**  
 * @Description: 极速贷获取续期详情
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年8月28日
 */
@Component("getRenewalDetailApi")
public class GetRenewalDetailApi implements JsdH5Handle {

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    private JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    private JsdBorrowCashService jsdBorrowCashService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Resource
    private JsdResourceService jsdResourceService;

	@Override
	public JsdH5HandleResponse process(Context context) {
		// 西瓜借款borrowNo
		String tradeNoXgxy = ObjectUtils.toString(context.getDataMap().get("borrowNo"), "");
		// 请求时间戳
		String timestamp = ObjectUtils.toString(context.getDataMap().get("timestamp"), "");
		
		// 借款记录
		JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
		if (borrowCashDo == null) {
			throw new FanbeiException("borrowCash is not exist", FanbeiExceptionCode.SYSTEM_ERROR);
		}
		logger.info("getRenewalDetail jsdBorrowCash record = {} " , borrowCashDo);

		jsdBorrowCashRenewalService.checkCanRenewal(borrowCashDo);
		
		Map<String, Object> data = Maps.newHashMap();
		if("v1".equals(borrowCashDo.getVersion())) { // v1 赊销
			data.put("delayInfo", jsdBorrowCashRenewalService.getRenewalDetail(borrowCashDo));
			
		} else{		// v2 砍头
			data.put("delayInfo", jsdBorrowCashRenewalService.getBeheadRenewalDetail(borrowCashDo));
			
		}
		
		data.put("timestamp", timestamp);
		JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功", data);

		return resp;
	}
}

