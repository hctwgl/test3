package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGameResultDo;
import com.ald.fanbei.api.dal.domain.dto.AfGameResultDto;

public interface AfGameResultDao {
	   /**
	    * 增加记录
	    * @param afGameResultDo
	    * @return
	    */
	    int addGameResult(AfGameResultDo afGameResultDo);
	    
	    /**
	     * 获取最近中奖纪录
	     * @return
	     */
	    List<AfGameResultDo> getLatestRecord();
	    
	    /**
	     * 获取用户中奖情况
	     * @param userId
	     * @return
	     */
	    List<AfGameResultDto> getResultDtoByUserId(Long userId);

		List<AfGameResultDo> getTearPacketResultByUserId(@Param("userId")Long userId, @Param("borrowId")Long borrowId);

		List<AfGameResultDo> getTearPacketLatestRecord();
}
