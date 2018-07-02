package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfFundSideBorrowCashDo;

/**
 * 资金方放款与用户借款记录关联表Dao
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:55:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfFundSideBorrowCashDao extends BaseDao<AfFundSideBorrowCashDo, Long> {

	/**
	 * 根据借款id获取对应关联记录
	 * @param borrowCashId
	 * @return
	 */
    public AfFundSideBorrowCashDo getRecordByBorrowCashId(@Param("borrowCashId")Long borrowCashId);

}
