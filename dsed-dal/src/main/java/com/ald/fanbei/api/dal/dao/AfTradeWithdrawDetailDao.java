package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfTradeWithdrawDetailDo;

/**
 * 商圈商户提现记录表Dao
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-19 09:29:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeWithdrawDetailDao extends BaseDao<AfTradeWithdrawDetailDo, Long> {

	List<AfTradeWithdrawDetailDo> getByRecordId(@Param("recordId")Long recordId);

    

}
