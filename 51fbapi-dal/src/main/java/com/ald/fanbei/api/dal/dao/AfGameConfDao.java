package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

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

	/**
	 * 根据id,code获取游戏配置
	 * @param id
	 * @param gameCode
	 * @return
	 */
	AfGameConfDo getByIdAndCode(@Param("id")Long id, @Param("gameCode")String gameCode);


}
