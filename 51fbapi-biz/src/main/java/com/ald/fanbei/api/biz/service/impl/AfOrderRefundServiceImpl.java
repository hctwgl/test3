package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfOrderRefundService;
import com.ald.fanbei.api.dal.dao.AfOrderRefundDao;
import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月29日下午3:54:32
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afOrderRefundService")
public class AfOrderRefundServiceImpl implements AfOrderRefundService {

	@Resource
	AfOrderRefundDao afOrderRefundDao;
	
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

}
