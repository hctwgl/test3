package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfPromotionLogsDo;

public interface AfPromotionLogsDao {
	/**
	 * 增加记录
	 * 
	 * @param afPromotionLogsDo
	 * @return
	 */
	int addPromotionLogs(AfPromotionLogsDo afPromotionLogsDo);

	/**
	 * 更新记录
	 * 
	 * @param afPromotionLogsDo
	 * @return
	 */
	int updatePromotionLogs(AfPromotionLogsDo afPromotionLogsDo);
}
