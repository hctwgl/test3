package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfPromotionChannelDo;

/**
 * 
 * @类描述：
 * 
 * @author huyang 2017年4月18日下午2:39:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfPromotionChannelService {

	/**
	 * @方法说明：根据主键查询
	 * @author huyang
	 * @param id
	 * @return
	 */
	public AfPromotionChannelDo getById(Long id);
}
