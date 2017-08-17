package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.query.AfShopQuery;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月23日下午2:15:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfShopDao {
	
	AfShopDo getShopById(@Param("shopId")Long shopId);
	
	AfShopDo getShopByPlantNameAndTypeAndServiceProvider(@Param("platformName")String platformName, @Param("type")String type, @Param("serviceProvider")String serviceProvider);
	
	List<AfShopDo> getShopList(AfShopQuery query);
	
	
	/**
	 * 获取预发布环境的商城
	 * @param query
	 * @return
	 */
	List<AfShopDo> getPreEnvShopList(AfShopQuery query);

	AfShopDo getShopInfoBySecType(String secType);

	AfShopDo getShopInfoBySecType(AfShopDo afShopDo);
	AfShopDo getShopInfoBySecTypeOpen(AfShopDo afShopDo);
}
