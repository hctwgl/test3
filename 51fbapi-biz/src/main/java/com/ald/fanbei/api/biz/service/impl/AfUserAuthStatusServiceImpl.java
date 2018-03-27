package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.enums.UserAuthSceneStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.dal.dao.AfUserAuthStatusDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 额度拆分多场景认证状体记录ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:58:48 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afUserAuthStatusService")
public class AfUserAuthStatusServiceImpl extends ParentServiceImpl<AfUserAuthStatusDo, Long> implements AfUserAuthStatusService {

    private static final Logger logger = LoggerFactory.getLogger(AfUserAuthStatusServiceImpl.class);

    @Resource
    private AfUserAuthStatusDao afUserAuthStatusDao;

    @Override
    public BaseDao<AfUserAuthStatusDo, Long> getDao() {
	return afUserAuthStatusDao;
    }

    @Override
    public void addOrUpdateAfUserAuthStatus(AfUserAuthStatusDo afUserAuthStatusDo) {
        if (afUserAuthStatusDao.selectAfUserAuthStatusByUserIdAndScene(afUserAuthStatusDo.getUserId(), afUserAuthStatusDo.getScene()) > 0) {
            if (!UserAuthSceneStatus.YES.getCode().equals(afUserAuthStatusDo.getStatus()) && StringUtils.isNotBlank(afUserAuthStatusDo.getCauseReason())) {
                JSONArray jsonArray = JSON.parseArray(afUserAuthStatusDo.getCauseReason());
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String status = jsonObject.getString("status");
                    if (YesNoStatus.NO.getCode().equals(status)) {
                        afUserAuthStatusDo.setStatus("N");
                        break;
                    }
                    afUserAuthStatusDo.setStatus("P");
                }
            }
            afUserAuthStatusDao.updateAfUserAuthStatus(afUserAuthStatusDo);
        } else {
            afUserAuthStatusDao.addAfUserAuthStatus(afUserAuthStatusDo);
        }
    }

    @Override
    public AfUserAuthStatusDo selectAfUserAuthStatusByCondition(Long userId, String scene, String status) {

	   return afUserAuthStatusDao.selectAfUserAuthStatusByCondition(userId,scene,status);
    }

    @Override
    public AfUserAuthStatusDo getAfUserAuthStatusByUserIdAndScene(Long userId, String scene) {
        return afUserAuthStatusDao.getAfUserAuthStatusByUserIdAndScene(userId, scene);
    }

    @Override
    public List<AfUserAuthStatusDo> selectAfUserAuthStatusByUserIdAndStatus(Long userId, String status) {
        return afUserAuthStatusDao.selectAfUserAuthStatusByUserIdAndStatus(userId,status);
    }

    @Override
    public List<AfUserAuthStatusDo> selectAfUserAuthStatusByUserId(Long userId) {
	return afUserAuthStatusDao.selectAfUserAuthStatusByUserId(userId);
    }

    @Override
    public boolean isPass(String scene, Long userId) {
    	AfUserAuthStatusDo uas = afUserAuthStatusDao.getAfUserAuthStatusByUserIdAndScene(userId, scene);
    	if(YesNoStatus.YES.getCode().equals(uas.getStatus())) {
    		return true;
    	}
    	return false;
    }

	@Override
	public String getBldOpenStatus(Long userId) {
		AfUserAuthStatusDo authStatus = this.getAfUserAuthStatusByUserIdAndScene(userId, SceneType.BLD_LOAN.getName());
		if(authStatus != null && StringUtils.isNotBlank(authStatus.getStatus())) {
			return authStatus.getStatus();
		}
		return "N";
	}

	@Override
	public int updateAfUserAuthStatusByUserId(Long userId, String scene,
			String status) {
		return afUserAuthStatusDao.updateAfUserAuthStatusByUserId(userId, scene, status);
	}

	@Override
	public int updateAfUserAuthStatus(Long userId, String scene, String status) {
		return afUserAuthStatusDao.insertAfUserAuthStatus(userId, scene, status);
	}
}