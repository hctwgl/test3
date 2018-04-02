package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfFacescoreShareCountDo;

/**
 * 颜值测试红包分享次数记录实体类Service
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-19 09:39:51
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfFacescoreShareCountService extends ParentService<AfFacescoreShareCountDo, Long>{

	void dealWithShareCount(Long userId);

	AfFacescoreShareCountDo getByUserId(Long userId);

}
