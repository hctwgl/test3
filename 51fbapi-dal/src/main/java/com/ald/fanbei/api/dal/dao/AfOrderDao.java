package com.ald.fanbei.api.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.query.AfOrderQuery;

/**
 * @类描述：
 * @author hexin 2017年2月16日下午15:07:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderDao {

	/**
	 * 新增订单
	 * @param afOrder
	 * @return
	 */
	int createOrder(AfOrderDo afOrder);
	
	/**
	 * 修改订单
	 * @param afOrder
	 * @return
	 */
	int updateOrderByOrderNo(AfOrderDo afOrder);
	
	/**
	 * @param startDate
	 * @param endDate
	 * @param orderType
	 * @return
	 */
	public String getCurrentLastOrderNo(@Param("startDate")Date startDate,@Param("endDate")Date endDate, @Param("orderType")String orderType);
	
	/**
	 * 获取订单详情
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoById(@Param("rid")Long rid,@Param("userId")Long userId);
	
	/**
	 * 获取订单详情
	 * @param rid
	 * @return
	 */
	public AfOrderDo getOrderInfoByOrderNo(@Param("orderNo")String orderNo);
	
	/**
	 * 获取订单列表
	 * @param query
	 * @return
	 */
	public List<AfOrderDo> getOrderListByStatus(AfOrderQuery query);
}
