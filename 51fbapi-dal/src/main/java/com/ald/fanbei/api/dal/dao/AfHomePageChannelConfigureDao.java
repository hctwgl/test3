package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfHomePageChannelConfigureDo;

/**
 * 频道配置表Dao
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-04-12 17:59:56
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfHomePageChannelConfigureDao extends BaseDao<AfHomePageChannelConfigureDo, Long> {

	List<AfHomePageChannelConfigureDo> getByChannelId(@Param("tabId")Long tabId);

    

}
