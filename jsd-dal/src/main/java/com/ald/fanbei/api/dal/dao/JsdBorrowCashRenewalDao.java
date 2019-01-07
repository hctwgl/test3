package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;

import java.math.BigDecimal;
import java.util.List;

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

	JsdBorrowCashRenewalDo getByTradeNo(String tradeNo);

	JsdBorrowCashRenewalDo getByTradeNoXgxy(String tardeNoXgxy);

	List<JsdBorrowCashRenewalDo> getJsdRenewalByBorrowId(Long borrowId);

	List<JsdBorrowCashRenewalDo> getJsdRenewalByBorrowIdAndStatus(Long borrowId);

	List<JsdBorrowCashRenewalDo> getMgrJsdRenewalByBorrowId(Long borrowId);

	/**
	 * 获取结算系统实收数据
	 * @Param list {@link FinaneceDataDo} 对象
	 *@return  <code>List<code/>
	 *
	 * **/
	List<FinaneceDataDo> getRenewalData();
	
	
    BigDecimal getRenewalAmount(JsdBorrowCashRenewalDo jsdBorrowCashRenewalDo);
	

}
