package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfOrderRefundService;
import com.ald.fanbei.api.biz.service.AfTradeSettleOrderService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;
import com.ald.fanbei.api.dal.domain.AfTradeSettleOrderDo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Date;

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
	@Resource
	AfAftersaleApplyDao afAftersaleApplyDao;
	@Resource
	AfTradeOrderDao afTradeOrderDao;
	@Resource
	AfSupplierOrderSettlementDao afSupplierOrderSettlementDao;
	
	@Resource
	AfTradeSettleOrderService afTradeSettleOrderService;
	
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
                    		    if (!OrderType.BOLUOME.getCode().equals(orderInfo.getOrderType())) {
                    			if (orderRefundInfo.getAftersaleApplyId() > 0) {
                    			    AfOrderDo orderT = new AfOrderDo();
                    			    orderT.setRid(orderInfo.getRid());
                    			    orderT.setStatus(OrderStatus.CLOSED.getCode());
                    			    orderInfo.setClosedReason(StringUtil.isBlank(orderInfo.getClosedReason()) ? "系统关闭" : orderInfo.getClosedReason());
                    			    orderInfo.setClosedDetail(StringUtil.isBlank(orderInfo.getClosedDetail()) ? "退款完成" : orderInfo.getClosedDetail());
                    			    orderInfo.setGmtClosed(new Date());
                    			    afOrderDao.updateOrder(orderT);
                    
                    			    AfAftersaleApplyDo saleDo = new AfAftersaleApplyDo();
                    			    saleDo.setId(orderRefundInfo.getAftersaleApplyId());
                    			    saleDo.setStatus(AfAftersaleApplyStatus.FINISH.getCode());
                    			    afAftersaleApplyDao.updateById(saleDo);
                    			}
                    		    }
                    		    else {
                    			 AfOrderDo orderT = new AfOrderDo();
                 			    orderT.setRid(orderInfo.getRid());
                 			    orderT.setStatus(OrderStatus.CLOSED.getCode());
                 			    orderInfo.setClosedReason(StringUtil.isBlank(orderInfo.getClosedReason()) ? "系统关闭" : orderInfo.getClosedReason());
                 			    orderInfo.setClosedDetail(StringUtil.isBlank(orderInfo.getClosedDetail()) ? "退款完成" : orderInfo.getClosedDetail());
                 			    orderInfo.setGmtClosed(new Date());
                 			    afOrderDao.updateOrder(orderT);
				    }
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
				boluomeUtil.pushRefundStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.REFUND_SUC, orderInfo.getUserId(), orderRefundInfo.getAmount(), orderRefundInfo.getRefundNo());
			} else {
				boluomeUtil.pushRefundStatus(orderInfo.getRid(), orderInfo.getOrderNo(), orderInfo.getThirdOrderNo(), PushStatus.REFUND_FAIL, orderInfo.getUserId(), orderRefundInfo.getAmount(), orderRefundInfo.getRefundNo());
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
					orderInfo.setClosedReason(StringUtil.isBlank(orderInfo.getClosedReason())?"系统关闭":orderInfo.getClosedReason());
					orderInfo.setClosedDetail(StringUtil.isBlank(orderInfo.getClosedDetail())?"退款完成":orderInfo.getClosedDetail());
					orderInfo.setGmtClosed(new Date());
					updateOrderRefund(orderRefundInfo);
					afOrderDao.updateOrder(orderInfo);
					if(orderRefundInfo.getAftersaleApplyId()>0){
						AfAftersaleApplyDo saleDo =new AfAftersaleApplyDo();
						saleDo.setId(orderRefundInfo.getAftersaleApplyId());
						saleDo.setStatus(AfAftersaleApplyStatus.FINISH.getCode());
						afAftersaleApplyDao.updateById(saleDo);
					}
					//afSupplierOrderSettlementDao.updateStatusByOrderId(orderInfo.getRid());
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

	@Override
	public int dealWithTradeOrderRefund(final AfOrderRefundDo orderRefundInfo, final AfOrderDo orderInfo) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				logger.info("dealWithTradeOrderRefund begin: orderRefundInfo = {}, orderInfo = {}",orderRefundInfo, orderInfo);
				try {
					orderInfo.setStatus(OrderStatus.CLOSED.getCode());
					orderRefundInfo.setStatus(OrderRefundStatus.FINISH.getCode());
					updateOrderRefund(orderRefundInfo);
					afOrderDao.updateOrder(orderInfo);
					
					// modify luoxiao 结算单状态变更，退款不包含租房业务 ，只会有一条结算单 on 2018-04-10
//					AfTradeOrderDo tradeOrderDo = new AfTradeOrderDo();
//					tradeOrderDo.setOrderId(orderInfo.getRid());
//					tradeOrderDo.setStatus(TradeOrderStatus.REFUND.getCode());
//					tradeOrderDo.setGmtModified(new Date());
//					afTradeOrderDao.updateById(tradeOrderDo);
					
					AfTradeSettleOrderDo afTradeSettleOrderDo = new AfTradeSettleOrderDo();
	                afTradeSettleOrderDo.setOrderId(orderInfo.getRid());
	                afTradeSettleOrderDo.setStatus(AfTradeSettleOrderStatus.EXTRACTABLE.getCode());
	                afTradeSettleOrderDo.setModifier("SYSTEM");
	                afTradeSettleOrderDo.setGmtModified(new Date());
	                afTradeSettleOrderService.updateSettleOrder(afTradeSettleOrderDo);
	                // end by luoxiao on 2018-04-10
				} catch (Exception e) {
					logger.error("dealWithTradeOrderRefund  error: {}",e);
					status.setRollbackOnly();
					return 0;
				}
				return 1;
			}
		});
	}

	@Override
	public int dealWithTradeRefundFail(final AfOrderRefundDo orderRefundInfo, final AfOrderDo orderInfo) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				logger.info("dealWithTradeRefundFail begin: orderRefundInfo = {}, orderInfo = {}",orderRefundInfo, orderInfo);
				try {
					orderInfo.setStatus(OrderStatus.WAITING_REFUND.getCode());
					orderRefundInfo.setStatus(OrderRefundStatus.FAIL.getCode());
					updateOrderRefund(orderRefundInfo);
					afOrderDao.updateOrder(orderInfo);
					
					// modify luoxiao 结算单状态变更，退款不包含租房业务 ，只会有一条结算单 on 2018-04-10
//					AfTradeOrderDo tradeOrderDo = new AfTradeOrderDo();
//					tradeOrderDo.setOrderId(orderInfo.getRid());
//					tradeOrderDo.setStatus(TradeOrderStatus.NEW.getCode());
//					tradeOrderDo.setGmtModified(new Date());
//					afTradeOrderDao.updateById(tradeOrderDo);
					
					AfTradeSettleOrderDo afTradeSettleOrderDo = new AfTradeSettleOrderDo();
	                afTradeSettleOrderDo.setOrderId(orderInfo.getRid());
	                afTradeSettleOrderDo.setStatus(AfTradeSettleOrderStatus.EXTRACTABLE.getCode());
	                afTradeSettleOrderDo.setModifier("SYSTEM");
	                afTradeSettleOrderDo.setGmtModified(new Date());
	                afTradeSettleOrderService.updateSettleOrder(afTradeSettleOrderDo);
	                // end by luoxiao on 2018-04-10
					
				} catch (Exception e) {
					logger.error("dealWithTradeRefundFail  error: {}",e);
					status.setRollbackOnly();
					return 0;
				}
				return 1;
			}
		});
	}

}