package com.ald.fanbei.api.biz.service.impl;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.BaseService;
/**
 *@类描述：
 *@author 何鑫 2017年16月20日 下午4:20:22
 *@注意：本内容仅限于杭州喜马拉雅家居有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
@Service("afOrderService")
public class AfOrderServiceImpl extends BaseService implements AfOrderService{

	@Override
	public int createOrderTrade(String content) {
		logger.info("createOrderTrade_content:"+content);
		return 0;
	}

	@Override
	public int updateOrderTradeSuccess(String content) {
		logger.info("updateOrderTradeSuccess_content:"+content);
		return 0;
	}

	@Override
	public int updateOrderTradeRefundCreated(String content) {
		logger.info("updateOrderTradeRefundCreated_content:"+content);
		return 0;
	}

	@Override
	public int updateOrderTradeRefundSuccess(String content) {
		logger.info("updateOrderTradeRefundSuccess_content:"+content);
		return 0;
	}

	@Override
	public int updateOrderTradePaidDone(String content) {
		logger.info("updateOrderTradePaidDone_content:"+content);
		return 0;
	}

	@Override
	public int updateOrderTradeClosed(String content) {
		logger.info("updateOrderTradeClosed_content:"+content);
		return 0;
	}

}
