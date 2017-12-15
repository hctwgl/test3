package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBoluomeActivityItemsDo;

/**
 * '第三方-上树请求记录Dao
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeActivityItemsDao extends BaseDao<AfBoluomeActivityItemsDo, Long> {

	AfBoluomeActivityItemsDo getItemsInfo(AfBoluomeActivityItemsDo itemsMessageSet);


	AfBoluomeActivityItemsDo getItemsInfoByOrderId(@Param("orderId") long orderId);

    

}
