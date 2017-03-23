package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfShopDo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月23日下午2:15:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfShopService {
	
	AfShopDo getShopById(Long shopId);
}
