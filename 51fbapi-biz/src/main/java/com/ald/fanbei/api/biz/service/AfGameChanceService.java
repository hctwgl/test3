/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGameChanceDo;

/**
 * @类现描述：
 * @author chenjinhu 2017年6月3日 下午6:41:55
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGameChanceService {
//	/**
//	 * 增加记录
//	 * 
//	 * @param afGameChanceDo
//	 * @return
//	 */
//	int addGameChance(AfGameChanceDo afGameChanceDo);

    /**
     * 更新用户机会数量
     * @param userId
     * @param type
     * @return
     */
    int updateGameChance(AfGameChanceDo afGameChanceDo);

	/**
	 * 获取用户机会列表
	 * 
	 * @param userId
	 * @return
	 */
	List<AfGameChanceDo> getByUserId(Long gameId,Long userId, String day);
	
	/**
	 * 更新邀请机会
	 * @param gameId
	 * @param userId
	 * @return
	 */
	int updateInviteChance(Long userId);
	
    /**
     * 分享游戏
     * @param userName
     */
    void dealWithShareGame(@Param("userName")String userName);
}
