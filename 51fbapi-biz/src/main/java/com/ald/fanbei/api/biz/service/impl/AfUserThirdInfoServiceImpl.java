package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserThirdInfoService;
import com.ald.fanbei.api.common.enums.UserThirdType;
import com.ald.fanbei.api.dal.dao.AfUserThirdInfoDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserThirdInfoDo;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * ServiceImpl
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-04 09:20:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUserThirdInfoService")
public class AfUserThirdInfoServiceImpl extends ParentServiceImpl<AfUserThirdInfoDo, Long>
		implements AfUserThirdInfoService {
	
    @Autowired
    private AfUserThirdInfoDao afUserThirdInfoDao;

    @Autowired
    private AfUserService afUserService;

	@Override
	public UserWxInfoDto getUserWxInfo(Long userId) {
		AfUserThirdInfoDo thirdInfo = getUserThirdInfoByUserId(userId, UserThirdType.WX.getCode());
		if (thirdInfo == null) return null;

		JSONObject jsonInfo = JSONObject.parseObject(thirdInfo.getThirdInfo());
		return new UserWxInfoDto(jsonInfo);
	}

	@Override
	public UserWxInfoDto getWxOrLocalUserInfo(Long userId) {
		UserWxInfoDto result = getUserWxInfo(userId);
		if (result == null) {
			result = new UserWxInfoDto();
			AfUserDo userDo = afUserService.getUserById(userId);
			result.setAvatar(userDo.getAvatar());
			result.setNick(userDo.getNick());
		}
		return result;
	}

	@Override
	public Long getUserIdByWxOpenId(String openId) {
		AfUserThirdInfoDo thirdInfo = getUserThirdInfoByThirdId(openId, UserThirdType.WX.getCode());
		if (thirdInfo == null) return null;

		return thirdInfo.getUserId();
	}

	// 获取用户第三方信息
	private AfUserThirdInfoDo getUserThirdInfoByUserId(Long userId, String userThirdType) {
		AfUserThirdInfoDo query = new AfUserThirdInfoDo();
		query.setUserId(userId);
		query.setThirdType(userThirdType);
		List<AfUserThirdInfoDo> list = getListByCommonCondition(query);
		return list.size() == 0 ? null : list.get(0);
	}

	// 获取用户第三方信息
	private AfUserThirdInfoDo getUserThirdInfoByThirdId(String thirdId, String userThirdType) {
		AfUserThirdInfoDo query = new AfUserThirdInfoDo();
		query.setThirdId(thirdId);
		query.setThirdType(userThirdType);
		List<AfUserThirdInfoDo> list = getListByCommonCondition(query);
		return list.size() == 0 ? null : list.get(0);
	}

	@Override
	public BaseDao<AfUserThirdInfoDo, Long> getDao() {
		return afUserThirdInfoDao;
	}

}