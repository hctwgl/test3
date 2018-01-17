package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalGoodsDo;

/**
 * Dao
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalGoodsDao extends BaseDao<AfBorrowLegalGoodsDo, Long> {

	Long getGoodsIdByProfitAmout(@Param("profitAmout")BigDecimal profitAmout);

	List<Long> getGoodsIdByProfitAmoutForV2(@Param("profitAmout")BigDecimal profitAmount);

}
