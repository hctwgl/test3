package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月29日下午3:53:02
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderRefundDao {

	int addOrderRefund(AfOrderRefundDo orderRefundInfo);
	
	int updateOrderRefund(AfOrderRefundDo orderRefundInfo);
	
	AfOrderRefundDo getOrderRefundByOrderId(@Param("orderId")Long orderId);
}
