package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfOrderTempDo;

/**
 * 
 * @类描述：AfUserOrderDao
 * @author hexin 2017年2月20日下午18:10:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderTempDao {

	/**
    * 增加记录
    * @param afUserOrderDo
    * @return
    */
    int addUserOrder(AfOrderTempDo afUserOrderDo);
    
    /**
     * 通过订单编号查询临时订单
     * @param orderNo
     * @return
     */
    AfOrderTempDo getByOrderNo(@Param("orderNo")String orderNo);
    
}
