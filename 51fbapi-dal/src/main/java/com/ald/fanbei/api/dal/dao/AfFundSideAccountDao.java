package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;

import com.ald.fanbei.api.dal.domain.AfFundSideAccountDo;

/**
 * '资金方账户资金信息表Dao
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:54:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfFundSideAccountDao extends BaseDao<AfFundSideAccountDo, Long> {

	/**
	 * 获取可用余额大于amount且启用的资金方账户信息
	 * @param amount
	 * @return
	 */
	public AfFundSideAccountDo getRandomOneAccountsByMinUsableMoney(BigDecimal amount);

    /**
     * 更新用户资金信息，在之前基础上累加
     * @param fundSideAccountDo
     * @return
     */
	public int updateRecordInfo(AfFundSideAccountDo fundSideAccountDo);
}
