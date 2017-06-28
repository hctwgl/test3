package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfOrderRefundService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.common.enums.OrderRefundStatus;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.PushStatus;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderRefundDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月29日下午3:54:32
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afOrderRefundService")
public class AfOrderRefundServiceImpl extends BaseService implements AfOrderRefundService {

	@Resource
	AfOrderRefundDao afOrderRefundDao;
	@Resource
	AfOrderDao afOrderDao;
	@Resource
	BoluomeUtil boluomeUtil;
	@Resource
	TransactionTemplate transactionTemplate;
	
	@Override
	public int addOrderRefund(AfOrderRefundDo orderRefundInfo) {
		return afOrderRefundDao.addOrderRefund(orderRefundInfo);
	}

	@Override
	public int updateOrderRefund(AfOrderRefundDo orderRefundInfo) {
		return afOrderRefundDao.updateOrderRefund(orderRefundInfo);
	}

	@Override
	public AfOrderRefundDo getOrderRefundByOrderId(Long orderId) {
		return afOrderRefundDao.getOrderRefundByOrderId(orderId);
	}

	@Override
	public String getCurrentLastRefundNo(Date current) {
		return afOrderRefundDao.getCurrentLastRefundNo(current);
	}

	@Override
	public AfOrderRefundDo getRefundInfoById(Long refundId) {
		return afOrderRefundDao.getRefundInfoById(refundId);
	}

	@Override
	public int dealWithOrderRefund(final AfOrderRefundDo orderRefundInfo,
			final AfOrderDo orderInfo, final boolean isBoluome) {
		Integer result = transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				logger.info("dealWithOrderRefund begin: orderRefundInfo = {}, orderInfo = {}",orderRefundInfo, orderInfo);
				try {
					orderRefundInfo.setStatus(OrderRefundStatus.FINISH.getCode());
					updateOrderRefund(orderRefundInfo);
				} catch (Exception e) {
					logger.error("dealWithOrderRefund  error = {}",e);
					status.setRollbackOnly();
					return 0;
				}
				return 1;
			}
		});
		if (isBoluome) {
			if (result == 1) {
				boluomeUtil.pushRefundStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.REFUND_SUC, orderInfo.getUserId(), orderInfo.getSaleAmount(), orderRefundInfo.getRefundNo());
			} else {
				boluomeUtil.pushRefundStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.REFUND_FAIL, orderInfo.getUserId(), orderInfo.getSaleAmount(), orderRefundInfo.getRefundNo());
			}
		}
		return result;
	}

	@Override
	public int dealWithSelfGoodsOrderRefund(final AfOrderRefundDo orderRefundInfo, final AfOrderDo orderInfo) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				logger.info("dealWithSelfGoodsOrderRefund begin: orderRefundInfo = {}, orderInfo = {}",orderRefundInfo, orderInfo);
				try {
					orderInfo.setStatus(OrderStatus.CLOSED.getCode());
					orderRefundInfo.setStatus(OrderRefundStatus.FINISH.getCode());
					updateOrderRefund(orderRefundInfo);
					afOrderDao.updateOrder(orderInfo);
				} catch (Exception e) {
					logger.error("dealWithSelfGoodsOrderRefund  error: {}",e);
					status.setRollbackOnly();
					return 0;
				}
				return 1;
			}
		});
	}

	@Override
	public int dealWithSelfGoodsOrderRefundFail(final AfOrderRefundDo orderRefundInfo, final AfOrderDo orderInfo) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				logger.info("dealWithSelfGoodsOrderRefundFail begin: orderRefundInfo = {}, orderInfo = {}",orderRefundInfo, orderInfo);
				try {
					orderInfo.setStatus(OrderStatus.WAITING_REFUND.getCode());
					orderRefundInfo.setStatus(OrderRefundStatus.FAIL.getCode());
					updateOrderRefund(orderRefundInfo);
					afOrderDao.updateOrder(orderInfo);
				} catch (Exception e) {
					logger.error("dealWithSelfGoodsOrderRefundFail  error: {}",e);
					status.setRollbackOnly();
					return 0;
				}
				return 1;
			}
		});
	}

}
