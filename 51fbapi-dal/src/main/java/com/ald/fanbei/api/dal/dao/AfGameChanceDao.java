package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGameChanceDo;

public interface AfGameChanceDao {
	   /**
	    * 增加记录
	    * @param afGameChanceDo
	    * @return
	    */
	    int addGameChance(AfGameChanceDo afGameChanceDo);
	    
	    /**
	     * 更新用户机会数量
	     * @param userId
	     * @param type
	     * @return
	     */
	    int updateGameChance(AfGameChanceDo afGameChanceDo);
	    
	    /**
	     * 获取用户机会列表
	     * @param userId
	     * @return
	     */
	    List<AfGameChanceDo> getByUserId(@Param("userId")Long userId,@Param("day")String day);
	    
	    
	    /**
	     * 获取用户机会列表
	     * @param userId
	     * @return
	     */
	    AfGameChanceDo getByUserIdType(@Param("userId")Long userId,@Param("type")String type,@Param("day")String day);
	    
	    /**
	     * 更新邀请用户机会次数和机会码
	     * @param rid
	     * @param count
	     * @param code
	     * @return
	     */
	    int updateInviteGameChance(@Param("rid")Long rid,@Param("count")Integer count,@Param("code")String code);
}
