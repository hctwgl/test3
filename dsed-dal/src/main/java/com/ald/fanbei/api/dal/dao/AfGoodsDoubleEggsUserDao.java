package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsUserDo;

/**
 * 双蛋活动Dao
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-12-07 14:47:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsDoubleEggsUserDao extends BaseDao<AfGoodsDoubleEggsUserDo, Long> {

	int isExist(@Param("doubleGoodsId")Long doubleGoodsId, @Param("userId")Long userId);

	int isSubscribed(@Param("userId")Long userId,@Param("doubleGoodsId") Long doubleGoodsId);

	int getSpringFestivalNumber(@Param("goodsId")Long goodsId);

}
