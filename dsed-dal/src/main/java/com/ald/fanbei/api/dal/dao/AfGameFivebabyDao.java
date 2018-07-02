package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfGameFivebabyDo;

public interface AfGameFivebabyDao {
	/**
	 * 增加记录
	 * 
	 * @param afGameFivebabyDo
	 * @return
	 */
	int addGameFivebaby(AfGameFivebabyDo afGameFivebabyDo);

	/**
	 * 更新记录
	 * 
	 * @param afGameFivebabyDo
	 * @return
	 */
	int updateGameFivebaby(AfGameFivebabyDo afGameFivebabyDo);
	
	/**
	 * 获取用户五娃数量
	 * @param userId
	 * @return
	 */
	AfGameFivebabyDo getByUserId(Long userId);
	
//	/**
//	 * 获取最近中奖人
//	 * @param userId
//	 * @return
//	 */
//	List<AfGameFivebabyDo> getLatestAwards();
}
