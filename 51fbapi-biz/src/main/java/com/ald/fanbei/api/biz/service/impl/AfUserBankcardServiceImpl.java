package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;

/**
 *@类现描述：
 *@author hexin 2017年2月18日 下午17:25:37
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserBankcardService")
public class AfUserBankcardServiceImpl implements AfUserBankcardService{

	@Resource
	private AfUserBankcardDao afUserBankcardDao;
	
	@Override
	public AfUserBankcardDo getUserMainBankcardByUserId(Long userId) {
		return afUserBankcardDao.getUserMainBankcardByUserId(userId);
	}

	@Override
	public List<AfUserBankcardDo> getUserBankcardByUserId(Long userId) {
		return afUserBankcardDao.getUserBankcardByUserId(userId);
	}

	@Override
	public int deleteUserBankcardByIdAndUserId(Long userId, Long rid) {
		return afUserBankcardDao.deleteUserBankcardByIdAndUserId(userId, rid);
	}

}
