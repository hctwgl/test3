package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfAppUpgradeDo;


/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月20日下午6:13:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAppUpgradeService {

	AfAppUpgradeDo getAppUpgradeById(Long id);
	
	/**
	 * 获取范围为全部升级的最大版本
	 *@return
	 */
	AfAppUpgradeDo getMaxAppUpgradeVersion();
	
	/**
	 * 获取范围为部分升级,并且clientVersion 再最小升级和最大升级范围的版本
	 *@param clientVersion
	 *@return
	 */
	AfAppUpgradeDo getNewestAppUpgradeVersion(Integer versionCode);
	AfAppUpgradeDo getNewestIOSVersionBySpecify(Integer versionCode);

	
}
