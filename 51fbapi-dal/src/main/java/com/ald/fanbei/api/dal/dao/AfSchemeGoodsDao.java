/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfSchemeGoodsDo;



/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年6月6日下午1:57:22
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSchemeGoodsDao {
	
	/**
	 * 根据goodsId获取优惠计划
	 * @param id
	 * @return
	 */
	AfSchemeGoodsDo getSchemeGoodsByGoodsId(Long goodsId);
	
	
}
