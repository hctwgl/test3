package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserInvitationDto;

/**
 *@类描述：
 *@author Xiaotianjian 2017年1月19日下午1:52:02
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserService")
public class AfUserServiceImpl implements AfUserService {

	@Resource
	AfUserDao afUserDao;
	@Resource
	AfUserAuthDao afUserAuthDao;
	
	@Override
	public int addUser(AfUserDo afUserDo) {//TODO 事物
		int result = afUserDao.addUser(afUserDo);
		AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
		afUserAuthDo.setUserId(afUserDo.getRid());
		afUserAuthDao.addUserAuth(afUserAuthDo);
		return result;
	}

	@Override
	public AfUserDo getUserById(Long userId) {
		return afUserDao.getUserById(userId);
	}

	@Override
	public int updateUser(AfUserDo afUserDo) {
		return afUserDao.updateUser(afUserDo);
	}

	@Override
	public AfUserDo getUserByUserName(String userName) {
		return afUserDao.getUserByUserName(userName);
	}

	
	@Override
	public AfUserDo getUserByRecommendCode(String recommendCode) {
		return afUserDao.getUserByRecommendCode(recommendCode);
	}

	
	@Override
	public List<AfUserInvitationDto> getRecommendUserByRecommendId(Long recommendId,Integer start,Integer end ) {
		return afUserDao.getRecommendUserByRecommendId(recommendId,start,end );
	}

}
