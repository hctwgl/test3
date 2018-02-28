package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.dal.dao.AfUserAccountSenceDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;

/**
 * 额度拆分多场景分期额度记录ServiceImpl
 *
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:57:51 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afUserAccountSenceService")
public class AfUserAccountSenceServiceImpl extends ParentServiceImpl<AfUserAccountSenceDo, Long> implements AfUserAccountSenceService {

    private static final Logger logger = LoggerFactory.getLogger(AfUserAccountSenceServiceImpl.class);

    @Resource
    private AfUserAccountSenceDao afUserAccountSenceDao;

    @Override
    public BaseDao<AfUserAccountSenceDo, Long> getDao() {
        return afUserAccountSenceDao;
    }

    @Override
    public int updateUserSceneAuAmount(String scene, Long userId, BigDecimal auAmount) {
        return afUserAccountSenceDao.updateUserSceneAuAmount(scene, userId, auAmount);
    }

    @Override
    public AfUserAccountSenceDo getByUserIdAndType(String scene, Long userId) {
        return afUserAccountSenceDao.getByUserIdAndType(scene, userId);
    }

    @Override
    public String getBusinessTypeByOrderId(Long orderId) {
        return afUserAccountSenceDao.getBusinessTypeByOrderId(orderId);
    }

    @Override
    public AfUserAccountSenceDo getByUserIdAndScene(String scene, Long userId) {

	return afUserAccountSenceDao.getByUserIdAndScene(scene, userId);
    }

    @Override
    public List<AfUserAccountSenceDo> getByUserId(Long userId) {
        return afUserAccountSenceDao.getByUserId(userId);
    }
    
    @Override
    public BigDecimal getAuAmountByScene(String scene, Long userId) {
    	AfUserAccountSenceDo accScene = afUserAccountSenceDao.getByUserIdAndScene(scene, userId);
    	if(accScene != null) {
    		return accScene.getAuAmount();
    	}else {
    		return BigDecimal.ZERO;
    	}
    }

	@Override
	public AfUserAccountSenceDo buildAccountScene(Long userId, String loanType, String amount) {
		AfUserAccountSenceDo authAccountSceneDo = new AfUserAccountSenceDo();
		authAccountSceneDo.setUserId(userId);
		authAccountSceneDo.setScene(loanType);
		authAccountSceneDo.setAuAmount(new BigDecimal(amount));
		return authAccountSceneDo;
	}

	@Override
	public void saveOrUpdateAccountSence(AfUserAccountSenceDo accountSenceDo) {
		AfUserAccountSenceDo accountSence = afUserAccountSenceDao.getByUserIdAndScene(accountSenceDo.getScene(), accountSenceDo.getUserId());
		if(accountSence == null) {
			afUserAccountSenceDao.saveRecord(accountSenceDo);
		} else {
			afUserAccountSenceDao.updateById(accountSenceDo);
		}
		
	}

	@Override
	public BigDecimal getTotalAmount(Long userId) {
		AfUserAccountSenceDo sceneDo = afUserAccountSenceDao.getByUserIdAndScene(SceneType.LOAN_TOTAL.getName(), userId);
		if(sceneDo != null) {
			return sceneDo.getAuAmount();
		}
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getBldUsedAmount(Long userId) {
		AfUserAccountSenceDo sceneDo = afUserAccountSenceDao.getByUserIdAndScene(SceneType.BLD_LOAN.getName(), userId);
		if(sceneDo != null) {
			return sceneDo.getUsedAmount();
		}
		return BigDecimal.ZERO;
	}
}