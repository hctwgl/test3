/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfGameConfDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年6月4日 上午10:11:46
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGameConfService {
	/**
	 * 查询游戏配置
	 * @param gameId
	 * @return
	 */
	List<AfGameConfDo> getByGameId(Long gameId);
	
	/**
	 * 查询游戏配置
	 * @param gameId
	 * @return
	 */
	List<AfGameConfDo> getByGameCode(String gameCode);
}
