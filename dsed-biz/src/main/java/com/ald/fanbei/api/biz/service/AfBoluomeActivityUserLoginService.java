package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserLoginDo;

/**
 * '第三方-上树请求记录Service
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:39:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeActivityUserLoginService extends ParentService<AfBoluomeActivityUserLoginDo, Long>{

	Integer getBindingNum (Long activityId , Long refUserId);

	List<AfBoluomeActivityUserLoginDo> getByRefUserIdAndActivityId(Long userId,Long activityId);

	int saveUserLoginInfo(AfBoluomeActivityUserLoginDo afBoluomeActivityUserLogin);
}