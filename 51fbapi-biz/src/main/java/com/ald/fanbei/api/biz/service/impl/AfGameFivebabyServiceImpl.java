package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfGameFivebabyService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.dal.dao.AfGameFivebabyDao;
import com.ald.fanbei.api.dal.domain.AfGameFivebabyDo;

@Service("afGameFivebabyService")
public class AfGameFivebabyServiceImpl implements AfGameFivebabyService {

	@Resource
	AfGameFivebabyDao afGameFivebabyDao;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Override
	public int addGameFivebaby(AfGameFivebabyDo afGameFivebabyDo) {
		return afGameFivebabyDao.addGameFivebaby(afGameFivebabyDo);
	}

	@Override
	public int updateGameFivebaby(AfGameFivebabyDo afGameFivebabyDo) {
		return afGameFivebabyDao.updateGameFivebaby(afGameFivebabyDo);
	}

	@Override
	public AfGameFivebabyDo getByUserId(Long userId) {
		return afGameFivebabyDao.getByUserId(userId);
	}
	
//	@Override
//	public List<AfGameFivebabyDo> getLatestAwards() {
//		String key = Constants.CACHEKEY_LATESTAWARD_LIST;
//		List<AfGameFivebabyDo> resultList = bizCacheUtil.getObjectList(key);
//		if(resultList != null){
//			return resultList;
//		}
//		resultList = afGameFivebabyDao.getLatestAwards();
//		if(resultList != null){
//			bizCacheUtil.saveObjectList(key, resultList);
//		}
//		return resultList;
//	}

}
