package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBoluomeActivityItemsDo;
import com.ald.fanbei.api.dal.domain.AfCardDo;

/**
 * '第三方-上树请求记录Service
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeActivityItemsService extends ParentService<AfBoluomeActivityItemsDo, Long>{

    AfBoluomeActivityItemsDo getItemsInfoByOrderId(long orderId);

	List<AfCardDo> getUserCards(Long userId);

}
