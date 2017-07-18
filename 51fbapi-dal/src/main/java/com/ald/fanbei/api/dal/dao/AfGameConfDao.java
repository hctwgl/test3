package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfGameConfDo;

public interface AfGameConfDao {
	
	/**
	 * 查询游戏配置
	 * @param gameId
	 * @return
	 */
	List<AfGameConfDo> getByGameId(Long gameId);
	
	/**
	 * 查询游戏配置
	 * @param gameCode
	 * @return
	 */
	List<AfGameConfDo> getByGameCode(String gameCode);
}
