package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserThirdInfoService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.UserThirdType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserThirdInfoDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserThirdInfoDo;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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

	// 默认头像
	private static final String DEFAULT_AVATAR = "https://f.51fanbei.com/h5/app/activity/2018/05/openred_head.png";

    @Autowired
    private AfUserThirdInfoDao afUserThirdInfoDao;

    @Autowired
    private AfUserService afUserService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private BizCacheUtil bizCacheUtil;

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
			result.setAvatar(StringUtil.isBlank(userDo.getAvatar()) ? DEFAULT_AVATAR : userDo.getAvatar());
			result.setNick(userDo.getNick());
		}
		return result;
	}

	@Override
	public UserWxInfoDto getLocalUserInfoByWxOpenId(String openId) {
		return afUserThirdInfoDao.getLocalUserInfoByThirdId(openId, UserThirdType.WX.getCode());
	}

	@Override
	public Long getUserIdByWxOpenId(String openId) {
		UserWxInfoDto userWxInfoDto = getLocalUserInfoByWxOpenId(openId);
		if (userWxInfoDto == null) return null;

		return userWxInfoDto.getUserId();
	}

	@Override
	public AfUserThirdInfoDo bindUserWxInfo(final JSONObject userWxInfo, final Long userId, final String modifier) {
		String lock = "AfUserThirdInfoServiceImpl_bindUserWxInfo_lock_" + userId;
		boolean isLock = bizCacheUtil.getLockTryTimesSpecExpire(lock, lock,500, Constants.SECOND_OF_TEN_MINITS);
		if (isLock) {
			try {
				AfUserThirdInfoDo thirdInfo = getUserThirdInfoByUserId(userId, UserThirdType.WX.getCode());
				if (thirdInfo != null) {
					if (!thirdInfo.getThirdId().equals(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID))) {
						throw new FanbeiException("您已经有微信号绑定过此手机号了，不能再绑定了");
					} else {
						return thirdInfo;
					}
				}

				AfUserDo userDo = afUserService.getUserById(userId);
				String openId = userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID);
				AfUserThirdInfoDo userThirdInfoDo = new AfUserThirdInfoDo();
				userThirdInfoDo.setUserId(userId);
				userThirdInfoDo.setThirdId(openId);
				userThirdInfoDo.setThirdType(UserThirdType.WX.getCode());
				userThirdInfoDo.setCreator(modifier);
				userThirdInfoDo.setModifier(modifier);
				userThirdInfoDo.setThirdInfo(userWxInfo.toJSONString());
				userThirdInfoDo.setUserName(userDo.getUserName());
				saveRecord(userThirdInfoDo);
				return userThirdInfoDo;
			} finally {
				bizCacheUtil.delCache(lock);
			}
		} else {
			throw new RuntimeException(lock + "锁没有获取到");
		}
	}

	// 获取用户第三方信息
	public AfUserThirdInfoDo getUserThirdInfoByUserId(Long userId, String userThirdType) {
		AfUserThirdInfoDo query = new AfUserThirdInfoDo();
		query.setUserId(userId);
		query.setThirdType(userThirdType);
		List<AfUserThirdInfoDo> list = getListByCommonCondition(query);
		return list.size() == 0 ? null : list.get(0);
	}

	@Override
	public BaseDao<AfUserThirdInfoDo, Long> getDao() {
		return afUserThirdInfoDao;
	}

    @Override
    public int saveRecord(AfUserThirdInfoDo afUserThirdInfoDo){
        return afUserThirdInfoDao.saveRecord(afUserThirdInfoDo);
    }

    @Override
    public int selectUserThirdInfoByUserName(String userName){
        return afUserThirdInfoDao.selectUserThirdInfoByUserName(userName);
    }

    @Override
    public int updateByUserName(AfUserThirdInfoDo afUserThirdInfoDo){
        return afUserThirdInfoDao.updateByUserName(afUserThirdInfoDo);
    }


	@Override
	public List<AfUserThirdInfoDo> getListByCommonCondition(AfUserThirdInfoDo afUserThirdInfoDo){
		return afUserThirdInfoDao.getListByCommonCondition(afUserThirdInfoDo);
	}


}