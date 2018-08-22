package com.ald.fanbei.api.web.h5.api.jsd;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRenewalServiceImpl.JsdRenewalDealBo;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;

/**  
 * @Description: 极速贷  续期确认支付
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2018年8月22日
 */
@Component("jsdConfirmRenewalPayApi")
public class JsdConfirmRenewalPayApi implements DsedH5Handle {

	@Resource
	JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
	
	@Override
	public DsedH5HandleResponse process(Context context) {
		
		JsdRenewalDealBo bo = new JsdRenewalDealBo();
		
		Map<String, Object> resultMap = jsdBorrowCashRenewalService.doRenewal(bo);
		
		DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功", resultMap);
        return resp;
	}

	
	private JsdBorrowCashRenewalDo buildRenewalDo() {
		
		return null;
	}
	private JsdBorrowLegalOrderDo buildOrderDo() {
		
		return null;
	}
	private JsdBorrowLegalOrderCashDo buildOrderCashDo() {
		
		return null;
	}
	
}
