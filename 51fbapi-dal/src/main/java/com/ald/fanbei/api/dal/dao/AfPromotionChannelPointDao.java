package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfPromotionChannelPointDo;

public interface AfPromotionChannelPointDao {
	/**
	 * 增加记录
	 * 
	 * @param afPromotionChannelPointDo
	 * @return
	 */
	int addPromotionChannelPoint(AfPromotionChannelPointDo afPromotionChannelPointDo);

	/**
	 * 更新记录
	 * 
	 * @param afPromotionChannelPointDo
	 * @return
	 */
	int updatePromotionChannelPoint(AfPromotionChannelPointDo afPromotionChannelPointDo);

	/**
	 * @方法说明：TODO
	 * @author huyang
	 * @param channelCode
	 * @param pointCode
	 * @return
	 */
	AfPromotionChannelPointDo getPointByChannelCodeAndPointCode(@Param("channelCode") String channelCode, @Param("pointCode") String pointCode);

	/**
	 * @方法说明：增加访问数
	 * @author huyang
	 * @param id
	 */
	void addVisit(Long id);
	
	/**
	 * @方法说明：增加注册数
	 * @author huyang
	 * @param id
	 */
	void addRegister(Long id);
}
