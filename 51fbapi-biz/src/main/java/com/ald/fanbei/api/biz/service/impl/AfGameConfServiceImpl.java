/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfGameConfService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.AfGameConfDao;
import com.ald.fanbei.api.dal.domain.AfGameConfDo;

/**
 *@类现描述：游戏配置service类
 *@author chenjinhu 2017年6月4日 上午10:12:16
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afGameConfService")
public class AfGameConfServiceImpl implements AfGameConfService {

	@Resource
	AfGameConfDao afGameConfDao;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Override
	public List<AfGameConfDo> getByGameId(Long gameId) {
		String cacheKey = Constants.CACHEKEY_GAMECONF_INFO + gameId;
		List<AfGameConfDo> gameConfList = bizCacheUtil.getObjectList(cacheKey);
		if(gameConfList != null){
			return gameConfList;
		}
		gameConfList =  afGameConfDao.getByGameId(gameId);
		if(gameConfList != null){
			bizCacheUtil.saveObjectList(cacheKey, gameConfList);
		}
		return gameConfList;
	}
	
	@Override
	public List<AfGameConfDo> getByGameCode(String gameCode) {

		String cacheKey = Constants.CACHEKEY_GAMECONF_INFO + gameCode;
		List<AfGameConfDo> gameConfList = bizCacheUtil.getObjectList(cacheKey);
		if(gameConfList != null){
			return gameConfList;
		}
		gameConfList =  afGameConfDao.getByGameCode(gameCode);
		if(gameConfList != null){
			bizCacheUtil.saveObjectList(cacheKey, gameConfList);
		}
		return gameConfList;
	}

}
