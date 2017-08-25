package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserBankDidiRiskDo;

/**
 * 滴滴风控绑卡信息Dao
 * 
 * @author xiaotianjian
 * @version 1.0.0 初始化
 * @date 2017-08-14 13:41:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserBankDidiRiskDao extends BaseDao<AfUserBankDidiRiskDo, Long> {

	int saveRecordList(@Param("items")List<AfUserBankDidiRiskDo> list);

}
