package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowLegalGoodsDo;

/**
 * Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowLegalGoodsService extends ParentService<AfBorrowLegalGoodsDo, Long>{

	Long getGoodsIdByProfitAmout(BigDecimal profitAmout);

	List<Long> getGoodsIdByProfitAmoutForV2(BigDecimal profitAmount);

}
