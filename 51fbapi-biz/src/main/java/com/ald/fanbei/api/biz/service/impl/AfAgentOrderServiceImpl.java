/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfAgentOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfAgentOrderDto;
import com.taobao.api.domain.XItem;

/**
 * @类描述：
 * 
 * @author suweili 2017年4月18日下午3:45:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afAgentOrderService")
public class AfAgentOrderServiceImpl extends BaseService implements AfAgentOrderService {
	@Resource
	private GeneratorClusterNo generatorClusterNo;
	@Resource
	AfAgentOrderDao afAgentOrderDao;
	@Resource
	AfOrderDao afOrderDao;
	@Resource
	private TaobaoApiUtil taobaoApiUtil;
	@Resource
	private TransactionTemplate transactionTemplate;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	AfBorrowService afBorrowService;

	@Resource
	AfResourceService afResourceService;

	@Override
	public int addAgentOrder(AfAgentOrderDo afAgentOrderDo) {
		return afAgentOrderDao.addAgentOrder(afAgentOrderDo);
	}

	@Override
	public int updateAgentOrder(AfAgentOrderDo afAgentOrderDo) {
		return afAgentOrderDao.updateAgentOrder(afAgentOrderDo);
	}

	@Override
	public int insertAgentOrder(final AfAgentOrderDo afAgentOrderDo, final AfOrderDo afOrder) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					if (afOrder.getGoodsId() == null) {
						afOrder.setGoodsId(0L);
					}
					if (afOrder.getCount() == null) {
						afOrder.setCount(1);
					}
					if (afOrder.getRebateAmount() == null) {
						afOrder.setRebateAmount(new BigDecimal(0.00));
					}
					if (afOrder.getMobile() == null) {
						afOrder.setMobile("");
					}
					if (afOrder.getBankId() == null) {
						afOrder.setBankId(0L);
					}
					final String orderNo = generatorClusterNo.getOrderNo(OrderType.AGENTBUY);
					afOrder.setOrderNo(orderNo);
					afOrder.setOrderType(OrderType.AGENTBUY.getCode());
					Date gmtPayEnd = DateUtil.addMins(new Date(), 15);
					afOrder.setSecType("TAOBAO");
					afOrder.setShopName("");
					afOrder.setGmtPayEnd(gmtPayEnd);
					try {
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("numIid", afOrder.getNumId());
						List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();

						if (null != nTbkItemList && nTbkItemList.size() > 0) {
							XItem  item = nTbkItemList.get(0);
							String orderType = item.getMall()?"TMALL" : "TAOBAO";
							String title = item.getTitle();
							String pictUrl = item.getPicUrl();
							String nick = item.getNick();
							afOrder.setSecType(orderType);
							afOrder.setShopName(nick);
							afOrder.setGoodsName(title);
							afOrder.setGoodsIcon(pictUrl);
						}
					} catch (Exception e) {
						logger.error("this numId error_response", e);
					}

					afOrderDao.createOrder(afOrder);
					afAgentOrderDo.setOrderId(afOrder.getRid());
					afAgentOrderDao.addAgentOrder(afAgentOrderDo);

					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealMobileChargeOrder error:", e);
					return 0;
				}
			}
		});
	}

	@Override
	public AfAgentOrderDo getAgentOrderByOrderId(Long orderId) {

		return afAgentOrderDao.getAgentOrderByOrderId(orderId);
	}

	@Override
	public List<AfAgentOrderDto> getAgentOrderListByAgentId(Long agentId, String status, Integer start) {
		return afAgentOrderDao.getAgentOrderListByAgentId(agentId, status, start);
	}

	@Override
	public AfAgentOrderDto getAgentOrderInfoById(Long orderId) {
		return afAgentOrderDao.getAgentOrderInfoById(orderId);
	}

	@Override
	public int insertAgentOrderAndNper(final AfAgentOrderDo afAgentOrderDo, final AfOrderDo afOrder,
			final Integer nper) {
		return transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					if (afOrder.getGoodsId() == null) {
						afOrder.setGoodsId(0L);
					}
					if (afOrder.getCount() == null) {
						afOrder.setCount(1);
					}
					if (afOrder.getRebateAmount() == null) {
						afOrder.setRebateAmount(new BigDecimal(0.00));
					}
					if (afOrder.getMobile() == null) {
						afOrder.setMobile("");
					}
					if (afOrder.getBankId() == null) {
						afOrder.setBankId(0L);
					}

					final String orderNo = generatorClusterNo.getOrderNo(OrderType.AGENTBUY);
					afOrder.setOrderNo(orderNo);
					afOrder.setOrderType(OrderType.AGENTBUY.getCode());
					Date gmtPayEnd = DateUtil.addHoures(new Date(), Constants.ORDER_PAY_TIME_LIMIT);
					afOrder.setSecType("TAOBAO");
					afOrder.setShopName("");
					afOrder.setGmtPayEnd(gmtPayEnd);
					try {
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("numIid", afOrder.getNumId());
						List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();

						if (null != nTbkItemList && nTbkItemList.size() > 0) {
							XItem  item = nTbkItemList.get(0);
							String orderType = item.getMall()?"TMALL" : "TAOBAO";
							String title = item.getTitle();
							String pictUrl = item.getPicUrl();
							String nick = item.getNick();
							//计算预计返利信息，这里是预计返利
							BigDecimal tkRate=	new BigDecimal(item.getTkRate());
							AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.AppRebateRate.getCode());
							BigDecimal rebateAmount= afOrder.getActualAmount().multiply(tkRate).divide(new BigDecimal(10000)).multiply(new BigDecimal(resource.getValue()));
							afOrder.setRebateAmount(rebateAmount);
							afOrder.setSecType(orderType);
							afOrder.setShopName(nick);
							afOrder.setGoodsName(title);
							afOrder.setGoodsIcon(pictUrl);
						}
					} catch (Exception e) {
						logger.error("this numId error_response", e);
					}
					if (nper > 0) {
						afOrder.setNper(nper);
					}
					afOrderDao.createOrder(afOrder);

					afAgentOrderDo.setOrderId(afOrder.getRid());
					afAgentOrderDao.addAgentOrder(afAgentOrderDo);
					if(nper > 0){
						orderBorrowInfo(afOrder);
					}

					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					logger.info("dealMobileChargeOrder error:", e);
					return 0;
				}
			}
		});
	}

	public void orderBorrowInfo(AfOrderDo orderInfo) {
		orderInfo.setPayType(PayType.AGENT_PAY.getCode());
		orderInfo.setPayStatus(PayStatus.NOTPAY.getCode());
		orderInfo.setStatus(OrderStatus.NEW.getCode());
//		Long userId = orderInfo.getUserId();

		
		BorrowRateBo borrowRate = afResourceService.borrowRateWithResource(orderInfo.getNper());
		orderInfo.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
		afOrderDao.updateOrder(orderInfo);
		AfAgentOrderDo agentOrderDo = new AfAgentOrderDo();
		agentOrderDo.setOrderId(orderInfo.getRid());
		agentOrderDo.setBorrowRate(BorrowRateBoUtil.parseToDataTableStrFromBo(borrowRate));
		afAgentOrderDao.updateAgentOrder(agentOrderDo);

	}

}
