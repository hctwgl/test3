/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfAgentOrderDto;

/**
 * @类描述：
 * @author suweili 2017年4月18日下午3:44:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAgentOrderDao {
	 /**
	    * 增加记录
	    * @param afAgentOrderDo
	    * @return
	    */
	    int addAgentOrder(AfAgentOrderDo afAgentOrderDo);
	   /**
	    * 更新记录
	    * @param afAgentOrderDo
	    * @return
	    */
	    int updateAgentOrder(AfAgentOrderDo afAgentOrderDo);
	    /**
	     * 根据orderId获得代买详情
	     * @param orderId
	     * @return
	     */
	    AfAgentOrderDo getAgentOrderByOrderId(@Param("orderId")Long orderId);
	    /**
	     * 根据状态获取订单列表
	     * @param agentId
	     * @param status
	     * @return
	     */
	    List<AfAgentOrderDto> getAgentOrderListByAgentId(@Param("agentId")Long agentId,@Param("status")String status);
	    
	    /**
	     * 根据订单id获取订单信息
	     * @param orderId
	     * @return
	     */
	    AfAgentOrderDto getAgentOrderInfoById(@Param("orderId")Long orderId);
}
