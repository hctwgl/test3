package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.DsedAssetSideInfoDo;

/**
 * 信用卡绑定及订单支付Dao
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-07-16 11:48:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedAssetSideInfoDao extends BaseDao<DsedAssetSideInfoDo, Long> {

	DsedAssetSideInfoDo getByFlag(String assetSideEdspayFlag);


}
