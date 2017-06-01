package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAppUpgradeService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.dao.AfAppUpgradeDao;
import com.ald.fanbei.api.dal.domain.AfAppUpgradeDo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月7日下午3:07:38
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afAppUpgradeService")
public class AfAppUpgradeServiceImpl implements AfAppUpgradeService {

	@Resource
	AfAppUpgradeDao afAppUpgradeDao;
	
	@Resource
    BizCacheUtil bizCacheUtil;
	
	@Override
	public AfAppUpgradeDo getAppUpgradeById(Long id) {
		return afAppUpgradeDao.getAppUpgradeById(id);
	}

	@Override
	public AfAppUpgradeDo getMaxAppUpgradeVersion() {
		return afAppUpgradeDao.getMaxAppUpgradeVersion();
	}

	@Override
	public AfAppUpgradeDo getNewestAppUpgradeVersion(Integer versionCode) {
		String cacheKey = Constants.CACHEKEY_APK_NEWEST_VERSION + versionCode;
		AfAppUpgradeDo result = (AfAppUpgradeDo)bizCacheUtil.getObject(cacheKey);
        if(result != null){
            return result;
        }
        result = afAppUpgradeDao.getNewestVersionBySpecify(versionCode);
        if(result == null){
            result = afAppUpgradeDao.getMaxAppUpgradeVersion();
        } else {
        	bizCacheUtil.saveObject(cacheKey, result, Constants.SECOND_OF_TEN_MINITS);
        }
        return result;
	}

	@Override
	public AfAppUpgradeDo getNewestIOSVersionBySpecify(Integer versionCode, String channelCode) {
		return afAppUpgradeDao.getNewestIOSVersionBySpecify(versionCode, channelCode);
	}

	


}
