package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalGoodsDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalGoodsDo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalGoodsService;

/**
 * ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afBorrowLegalGoodsService")
public class AfBorrowLegalGoodsServiceImpl extends ParentServiceImpl<AfBorrowLegalGoodsDo, Long>
		implements AfBorrowLegalGoodsService {

	private static final Logger logger = LoggerFactory.getLogger(AfBorrowLegalGoodsServiceImpl.class);

	@Resource
	private AfBorrowLegalGoodsDao afBorrowLegalGoodsDao;

	@Override
	public BaseDao<AfBorrowLegalGoodsDo, Long> getDao() {
		return afBorrowLegalGoodsDao;
	}

	@Override
	public Long getGoodsIdByProfitAmout(BigDecimal profitAmout) {
		return afBorrowLegalGoodsDao.getGoodsIdByProfitAmout(profitAmout);
	}

	@Override
	public List<Long> getGoodsIdByProfitAmoutForV2(BigDecimal profitAmount) {
		return afBorrowLegalGoodsDao.getGoodsIdByProfitAmoutForV2(profitAmount);
	}
}