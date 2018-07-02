package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfAdInstalmentsDo;

/**
 * 分期商品管理Dao
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-09-21 11:07:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAdInstalmentsDao extends BaseDao<AfAdInstalmentsDo, Long> {


    AfAdInstalmentsDo getAdInfo();
}
