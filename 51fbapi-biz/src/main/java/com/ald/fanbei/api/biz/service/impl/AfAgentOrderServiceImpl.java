/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAgentOrderService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.dal.dao.AfAgentOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年4月18日下午3:45:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afAgentOrderService")
public class AfAgentOrderServiceImpl implements AfAgentOrderService {
	@Resource
	private GeneratorClusterNo generatorClusterNo;
	@Resource
	AfAgentOrderDao afAgentOrderDao;
	@Resource
	AfOrderDao afOrderDao;

	@Override
	public int addAgentOrder(AfAgentOrderDo afAgentOrderDo) {
		return afAgentOrderDao.addAgentOrder(afAgentOrderDo);
	}

	
	@Override
	public int updateAgentOrder(AfAgentOrderDo afAgentOrderDo) {
		return afAgentOrderDao.updateAgentOrder(afAgentOrderDo);
	}


	
	@Override
	public int insertAgentOrder(AfAgentOrderDo afAgentOrderDo, AfOrderDo afOrder) {
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
		afOrderDao.createOrder(afOrder);
		
		afAgentOrderDo.setOrderId(afOrder.getRid());
		
		
		return afAgentOrderDao.addAgentOrder(afAgentOrderDo);
	}

}
