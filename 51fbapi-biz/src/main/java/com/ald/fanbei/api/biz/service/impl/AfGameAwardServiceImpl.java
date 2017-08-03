/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfGameAwardService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.AfGameAwardDao;
import com.ald.fanbei.api.dal.domain.AfGameAwardDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年6月4日 上午11:36:54
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afGameAwardService")
public class AfGameAwardServiceImpl implements AfGameAwardService {

	@Resource
	AfGameAwardDao afGameAwardDao;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Override
	public int addGameAward(AfGameAwardDo afGameAwardDo) {
		return afGameAwardDao.addGameAward(afGameAwardDo);
	}

	@Override
	public AfGameAwardDo getByUserId(Long userId) {
		return afGameAwardDao.getByUserId(userId);
	}

	@Override
	public List<AfGameAwardDo> getLatestAwards() {
		String key = Constants.CACHEKEY_LATESTAWARD_LIST;
		List<AfGameAwardDo> awardList = bizCacheUtil.getObjectList(key);
		if(awardList != null){
			return awardList;
		}
		
		awardList = afGameAwardDao.getLatestAwards();
		if(awardList != null){
			bizCacheUtil.saveObjectList(key, awardList);
		}
		return awardList;
	}
	
	@Override
	public int updateContact(Long userId, String contacts) {
		return afGameAwardDao.updateContact(userId, contacts);
	}

}
