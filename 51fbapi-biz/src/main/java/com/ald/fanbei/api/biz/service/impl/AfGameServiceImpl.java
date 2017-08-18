package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfGameService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.AfGameDao;
import com.ald.fanbei.api.dal.domain.AfGameDo;

@Service("afGameService")
public class AfGameServiceImpl implements AfGameService {

	@Resource
	AfGameDao afGameDao;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Override
	public AfGameDo getByCode(String code) {
		String cacheKey = Constants.CACHEKEY_GAME_INFO + code;
		AfGameDo gameInfo = (AfGameDo)bizCacheUtil.getObject(cacheKey);
		if(gameInfo != null){
			return gameInfo;
		}
		gameInfo = afGameDao.getByCode(code);
		if(gameInfo != null){
			bizCacheUtil.saveObject(cacheKey, gameInfo);;
		}
		return gameInfo;
	}

}
