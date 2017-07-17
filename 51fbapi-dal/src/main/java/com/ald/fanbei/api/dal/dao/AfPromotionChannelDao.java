package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfPromotionChannelDo;

public interface AfPromotionChannelDao {
	/**
	 * 增加记录
	 * 
	 * @param afPromotionChannelDo
	 * @return
	 */
	int addPromotionChannel(AfPromotionChannelDo afPromotionChannelDo);

	/**
	 * 更新记录
	 * 
	 * @param afPromotionChannelDo
	 * @return
	 */
	int updatePromotionChannel(AfPromotionChannelDo afPromotionChannelDo);

	/**
	 * @方法说明：根据主键查询
	 * @author huyang
	 * @param id
	 * @return
	 */
	AfPromotionChannelDo getById(Long id);
}
