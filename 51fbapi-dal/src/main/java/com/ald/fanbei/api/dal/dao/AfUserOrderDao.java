package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserOrderDo;

/**
 * 
 * @类描述：AfUserOrderDao
 * @author hexin 2017年2月20日下午18:10:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserOrderDao {

	/**
    * 增加记录
    * @param afUserOrderDo
    * @return
    */
    int addUserOrder(AfUserOrderDo afUserOrderDo);
    
}
