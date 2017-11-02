/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;


import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfAgentOrderDto;

/**
 * @类描述：
 * @author suweili 2017年4月18日下午3:44:45
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAgentOrderService {
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
	     * 新增代买记录
	     * @param afAgentOrderDo
	     * @param afOrder
	     * @return
	     */
	    int insertAgentOrder(AfAgentOrderDo afAgentOrderDo,AfOrderDo afOrder);
	    
	    
	    int insertAgentOrderAndNper(AfAgentOrderDo afAgentOrderDo,AfOrderDo afOrder,Integer nper);
	    
	    /**
	     * 根据orderId查询代买记录
	     * @param orderId
	     * @return
	     */
	    AfAgentOrderDo getAgentOrderByOrderId(Long orderId);
	    
	    /**
	     * 根据状态获取订单列表
	     * @param agentId
	     * @param status
	     * @return
	     */
	    List<AfAgentOrderDto> getAgentOrderListByAgentId(Long agentId,String status,Integer start);
	    
	    /**
	     * 根据订单id获取订单信息
	     * @param orderId
	     * @return
	     */
	    AfAgentOrderDto getAgentOrderInfoById(Long orderId);
	    
	    
}
