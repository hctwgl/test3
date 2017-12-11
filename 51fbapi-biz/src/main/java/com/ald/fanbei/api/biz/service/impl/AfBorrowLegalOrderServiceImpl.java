package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;

/**
 * ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:14:21 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afBorrowLegalOrderService")
public class AfBorrowLegalOrderServiceImpl extends ParentServiceImpl<AfBorrowLegalOrderDo, Long>
		implements AfBorrowLegalOrderService {

	private static final Logger logger = LoggerFactory.getLogger(AfBorrowLegalOrderServiceImpl.class);

	@Resource
	private AfBorrowLegalOrderDao afBorrowLegalOrderDao;
	
	@Resource
	GeneratorClusterNo generatorClusterNo;

	@Override
	public BaseDao<AfBorrowLegalOrderDo, Long> getDao() {
		return afBorrowLegalOrderDao;
	}

	@Override
	public int saveBorrowLegalOrder(AfBorrowLegalOrderDo afBorrowLegalOrderDo) {
		String orderCashNo = generatorClusterNo.getOrderNo(OrderType.LEGAL);
		afBorrowLegalOrderDo.setOrderNo(orderCashNo);
		return afBorrowLegalOrderDao.saveRecord(afBorrowLegalOrderDo);
	}

}