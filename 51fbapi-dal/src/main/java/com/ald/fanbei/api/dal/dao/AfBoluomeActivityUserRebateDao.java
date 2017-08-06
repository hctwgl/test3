package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserRebateDo;
import com.ald.fanbei.api.dal.domain.query.AfBoluomeActivityUserRebateQuery;

/**
 * '第三方-上树请求记录Dao
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:39:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeActivityUserRebateDao extends BaseDao<AfBoluomeActivityUserRebateDo, Long> {

	AfBoluomeActivityUserRebateQuery getRebateCountNumber(AfBoluomeActivityUserRebateQuery userRebateQuery);



    

}
