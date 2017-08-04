package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.dal.domain.dto.AfTradeBusinessInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商圈商户信息表Service
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeBusinessInfoService extends ParentService<AfTradeBusinessInfoDo, Long>{

    List<AfTradeBusinessInfoDto> getByOrderId(@Param("orderId")Long orderId);

    AfTradeBusinessInfoDo getByBusinessId(@Param(("businessId"))Long businessId);
}
