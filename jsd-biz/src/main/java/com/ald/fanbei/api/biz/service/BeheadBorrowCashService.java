package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.jsd.ApplyBorrowCashBo.ApplyBorrowCashReq;
import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;

/**
 * 极速贷砍头Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-17 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface BeheadBorrowCashService extends ParentService<JsdBorrowCashDo, Long>{

	/**
	 * 借款申请-behead
	 * @param trialBo 
	 * @param mainCard 
	 * @param cashReq 
	 */
	void applyBeheadBorrowCash(ApplyBorrowCashReq cashReq, JsdUserBankcardDo mainCard, TrialBeforeBorrowBo trialBo);
    
    void dealBorrowSucc(Long cashId, String outTradeNo,String tradeDate);
    void dealBorrowFail(Long cashId, String outTradeNo, String failMsg);
    void dealBorrowFail(JsdBorrowCashDo cashDo, JsdBorrowLegalOrderDo orderDo, String failMsg);

	void resolve(TrialBeforeBorrowBo bo);

}
