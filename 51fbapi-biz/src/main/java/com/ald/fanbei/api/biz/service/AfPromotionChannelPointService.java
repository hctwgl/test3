package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfPromotionChannelPointDo;

/**
 * 
 * @类描述：
 * @author huyang 2017年4月18日下午1:55:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfPromotionChannelPointService {

	
	/**
	 * @方法说明：根据渠道Code和位置Code查询
	 * @author huyang
	 * @param channelCode 渠道编码
	 * @param pointCode 位置编码
	 * @return
	 */
	AfPromotionChannelPointDo getPoint(String channelCode, String pointCode);

	/**
	 * @方法说明：访问增加1
	 * @author huyang
	 * @param id
	 */
	void addVisit(Long id);

	/**
	 * @方法说明：注册增加1
	 * @author huyang
	 * @param id
	 */
	void addRegister(Long id);

}
