package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowLegalOrderQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;

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
	private AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;

	@Resource
	GeneratorClusterNo generatorClusterNo;

	@Override
	public BaseDao<AfBorrowLegalOrderDo, Long> getDao() {
		return afBorrowLegalOrderDao;
	}

	@Override
	public AfBorrowLegalOrderDo getLastBorrowLegalOrderByBorrowId(Long borrowId) {
		return afBorrowLegalOrderDao.getLastBorrowLegalOrderByBorrowId(borrowId);
	}

	@Override
	public AfBorrowLegalOrderDo getLastBorrowLegalOrderById(Long id) {
		return afBorrowLegalOrderDao.getById(id);
	}

	@Override
	public int saveBorrowLegalOrder(AfBorrowLegalOrderDo afBorrowLegalOrderDo) {
		String orderCashNo = generatorClusterNo.getOrderNo(OrderType.LEGAL);
		afBorrowLegalOrderDo.setOrderNo(orderCashNo);
		return afBorrowLegalOrderDao.saveRecord(afBorrowLegalOrderDo);
	}

	@Override
	public List<AfBorrowLegalOrderDo> getUserBorrowLegalOrderList(AfBorrowLegalOrderQuery query) {
		return afBorrowLegalOrderDao.getUserBorrowLegalOrderList(query);
	}
	
	public boolean isV2BorrowCash(Long borrowId) {
		Long orderId = afBorrowLegalOrderDao.tuchByBorrowId(borrowId);
		if(orderId != null) {
			Long orderCashId = afBorrowLegalOrderCashDao.tuchByBorrowId(borrowId);
			if(orderCashId == null) return true;
		}
		return false;
	}

	@Override
	public void checkIllegalVersionInvoke(Integer version, Long borrowId) {
		if (version < 405 && isV2BorrowCash(borrowId)) {
			throw new FanbeiException(FanbeiExceptionCode.MUST_UPGRADE_NEW_VERSION_REPAY);
		}
		
	}

	@Override
	public void updateSmartAddressScore(int smartAddressScore,long borrowId,String orderno) {
		 afBorrowLegalOrderDao.updateSmartAddressScore(smartAddressScore, borrowId,orderno);
	}

	@Override
	public AfBorrowLegalOrderDo getBorrowLegalOrderByBorrowId(Long borrowId) {
		return afBorrowLegalOrderDao.getBorrowLegalOrderByBorrowId(borrowId);
	}

}