/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfGameAwardDo;
import com.ald.fanbei.api.dal.domain.AfGameConfDo;

/**
 * @类现描述：
 * @author chenjinhu 2017年6月4日 上午11:35:22
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGameAwardService {

	/**
	 * 增加记录
	 * 
	 * @param afGameAwardDo
	 * @return
	 */
	int addGameAward(AfGameAwardDo afGameAwardDo);

	/**
	 * 通过用户id查询其获奖结果
	 * 
	 * @param userId
	 * @return
	 */
	AfGameAwardDo getByUserId(Long userId);

	/**
	 * 查询最近20条发奖记录
	 * 
	 * @return
	 */
	List<AfGameAwardDo> getLatestAwards();
	
	/**
	 * 更新联系人
	 * @param afGameAwardDo
	 * @return
	 */
	int updateContact(Long userId,String contacts);
	
	/**
	 * 获取借贷超市签到的奖励信息
	 * @param userId
	 * @param gameId
	 * @return
	 */
	AfGameAwardDo getLoanSignAward(Long userId,Long gameId);

	/**
	 * 领取签到奖
	 * @param userId
	 * @param confDo
	 */
	void receiveSignAward(Long userId, AfGameConfDo confDo);
}
