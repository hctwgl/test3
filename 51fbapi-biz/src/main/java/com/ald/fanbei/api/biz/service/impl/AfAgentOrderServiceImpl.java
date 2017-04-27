/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfAgentOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfAgentOrderDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.response.TbkItemInfoGetResponse;

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
						Date gmtPayEnd  = DateUtil.addMins(new Date(), 15);
						afOrder.setSecType("TAOBAO");
					    afOrder.setShopName("");
					    afOrder.setGmtPayEnd(gmtPayEnd);
					    try {
					    	TbkItemInfoGetResponse res = taobaoApiUtil.executeTakItemDetailSearch(afOrder.getNumId());
							
							JSONObject resObj = JSON.parseObject(res.getBody());
							JSONObject taoBaoInfo = resObj.getJSONObject("tbk_item_info_get_response").getJSONObject("results");

							JSONArray items = taoBaoInfo.getJSONArray("n_tbk_item");
							if(items.size()>0){
								JSONObject item = (JSONObject) items.get(0);
								afAgentOrderDo.setGoodsUrl(item.getString("item_url"));
							    String	orderType = item.getInteger("user_type")==0?"TAOBAO":"TMALL";
							    String price = item.getString("reserve_price");
							    String title = item.getString("title");
							    String pictUrl = item.getString("pict_url");
							    String salePrice = item.getString("zk_final_price");
							    String nick = item.getString("nick");
							    afOrder.setPriceAmount(new BigDecimal(price));
							    afOrder.setSaleAmount(new BigDecimal(salePrice));
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

	
	@Override
	public List<AfAgentOrderDto> getAgentOrderListByAgentId(Long agentId, String status,Integer start) {
		return afAgentOrderDao.getAgentOrderListByAgentId(agentId, status,start);
	}

	
	@Override
	public AfAgentOrderDto getAgentOrderInfoById(Long orderId) {
		return afAgentOrderDao.getAgentOrderInfoById(orderId);
	}

}
