package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;

import com.ald.fanbei.api.biz.bo.jsd.TrialBeforeBorrowBo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

/**
 * 极速贷Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashService extends ParentService<JsdBorrowCashDo, Long>{

    JsdBorrowCashDo getByBorrowNo(String borrowNo);

    boolean isCanBorrowCash(Long userId);

    String getCurrentLastBorrowNo(String orderNoPre);
    
    BigDecimal getRiskOriRate(String openId);
    
    void resolve(TrialBeforeBorrowBo bo);
    
    void dealBorrowSucc(Long cashId, String outTradeNo);
    
    void dealBorrowFail(Long cashId, String outTradeNo, String failMsg);

}
