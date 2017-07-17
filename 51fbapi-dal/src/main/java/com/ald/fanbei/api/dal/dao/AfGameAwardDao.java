/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGameAwardDo;

/**
 * @类现描述：
 * @author chenjinhu 2017年6月4日 上午11:32:38
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGameAwardDao {
	/**
	 * 增加记录
	 * 
	 * @param afGameAwardDo
	 * @return
	 */
	int addGameAward(AfGameAwardDo afGameAwardDo);
	
	/**
	 * 更新联系人
	 * @param afGameAwardDo
	 * @return
	 */
	int updateContact(@Param("userId")Long userId,@Param("contacts")String contacts);

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
}
