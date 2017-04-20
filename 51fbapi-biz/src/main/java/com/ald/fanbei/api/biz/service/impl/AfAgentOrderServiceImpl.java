/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.dal.dao.AfAgentOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.response.TaeItemDetailGetResponse;

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
						if(afOrder.getGoodsId()==null){
							afOrder.setGoodsId(0L);
						}
						if(afOrder.getCount()==null){
							afOrder.setCount(1);
						}
						if(afOrder.getRebateAmount()==null){
							afOrder.setRebateAmount(new BigDecimal(0.00));
						}
						if(afOrder.getMobile()==null){
							afOrder.setMobile("");
						}
						if(afOrder.getBankId()==null){
							afOrder.setBankId(0L);
						}
						final String orderNo = generatorClusterNo.getOrderNo(OrderType.AGENTBUY);
						afOrder.setOrderNo(orderNo);
						afOrder.setOrderType(OrderType.AGENTBUY.getCode());
						
						
//						TaeItemDetailGetResponse res = taobaoApiUtil.executeTaeItemDetailSearch(afOrder.getOpenId());
//						logger.info("createOrderTrade_content item is null res = {}", res);
//						JSONObject resObj = JSON.parseObject(res.getBody());
//						JSONObject taoBaoInfo = resObj.getJSONObject("tae_item_detail_get_response").getJSONObject("data");
//						
//
//						JSONObject sellerInfo = taoBaoInfo.getJSONObject("seller_info");
//					    String	orderType = sellerInfo.getString("seller_type").toUpperCase();
//					    String	shopName = sellerInfo.getString("shop_name").toUpperCase();
//
//					    afOrder.setShopName(shopName);
					    afOrder.setShopName("");

					    afOrder.setSecType("");
					    
						afOrderDao.createOrder(afOrder);
						
						afAgentOrderDo.setOrderId(afOrder.getRid());
						afAgentOrderDao.addAgentOrder(afAgentOrderDo);
			
			   return 1;
			} catch (Exception e) {
				status.setRollbackOnly();
				logger.info("dealMobileChargeOrder error:",e);
				return 0;
			}
		  }
		});
	}

	@Override
	public AfAgentOrderDo getAgentOrderByOrderId(Long orderId) {

		return afAgentOrderDao.getAgentOrderByOrderId(orderId);
	}

}
