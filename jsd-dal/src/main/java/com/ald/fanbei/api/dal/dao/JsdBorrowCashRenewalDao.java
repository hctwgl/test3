package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;

/**
 * 极速贷续期Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashRenewalDao extends BaseDao<JsdBorrowCashRenewalDo, Long> {

	JsdBorrowCashRenewalDo getLastJsdRenewalByBorrowId(Long borrowId);

	JsdBorrowCashRenewalDo getByRenewalNo(String renewalNo);

	JsdBorrowCashRenewalDo getRenewalByDelayNo(String delayNo);

    

}
