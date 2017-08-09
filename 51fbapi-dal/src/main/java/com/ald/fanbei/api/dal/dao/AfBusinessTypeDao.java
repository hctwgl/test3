package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBusinessTypeDo;

import java.util.List;

/**
 * 商户基础信息Dao
 * 
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2017-07-26 19:32:27
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBusinessTypeDao extends BaseDao<AfBusinessTypeDo, Long> {


    List<AfBusinessTypeDo> getList();   
}
