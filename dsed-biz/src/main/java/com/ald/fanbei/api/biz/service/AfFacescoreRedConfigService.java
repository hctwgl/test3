package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfFacescoreRedConfigDo;

/**
 * 颜值测试红包配置表实体类Service
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-12 16:36:56
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfFacescoreRedConfigService extends ParentService<AfFacescoreRedConfigDo, Long>{

	List<AfFacescoreRedConfigDo> findAll();

}
