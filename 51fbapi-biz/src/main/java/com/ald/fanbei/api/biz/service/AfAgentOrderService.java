/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;

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
	    
	    /**
	     * 根据orderId查询代买记录
	     * @param orderId
	     * @return
	     */
	    AfAgentOrderDo getAgentOrderByOrderId(Long orderId);
	    
	    
}
