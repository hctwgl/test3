package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAppUpgradeDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月20日下午6:14:10
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAppUpgradeDao {
	
	AfAppUpgradeDo getAppUpgradeById(@Param("id")Long id);
	
	AfAppUpgradeDo getMaxAppUpgradeVersion();
	
	AfAppUpgradeDo getNewestVersionBySpecify(@Param("versionCode")Integer versionCode);
	
	/**
	 * iOS获取最新版本
	 * @param versionCode
	 * @return
	 */
	AfAppUpgradeDo getNewestIOSVersionBySpecify(@Param("versionCode")Integer versionCode,@Param("appCode")String appCode);


}
