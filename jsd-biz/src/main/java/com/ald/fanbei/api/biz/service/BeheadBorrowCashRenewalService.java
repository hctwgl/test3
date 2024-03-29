package com.ald.fanbei.api.biz.service;

import java.util.Map;

import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRenewalServiceImpl.JsdRenewalDealBo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.alibaba.fastjson.JSONArray;

/**
 * 极速贷续期V2Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-12 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface BeheadBorrowCashRenewalService {

	Map<String, Object> dealRenewalV2(JsdRenewalDealBo paramBo);

	public long dealJsdRenewalSucess(String outTradeNo, String tradeNo);

	public long dealJsdRenewalFail(final String outTradeNo, final String tradeNo,boolean isNeedMsgNotice, String errorCode, String errorMsg);

	public JSONArray getBeheadRenewalDetail(JsdBorrowCashDo borrowCashDo);

}
