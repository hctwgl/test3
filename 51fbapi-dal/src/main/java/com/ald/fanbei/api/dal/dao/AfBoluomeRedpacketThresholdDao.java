package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBoluomeRedpacketThresholdDo;

/**
 * 点亮活动新版Dao
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeRedpacketThresholdDao extends BaseDao<AfBoluomeRedpacketThresholdDo, Long> {

	List<AfBoluomeRedpacketThresholdDo> getAllThreadholds();



    

}
