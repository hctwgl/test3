package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdTotalInfoDo;

import java.util.List;

/**
 * Service
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2019-01-03 13:49:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdTotalInfoService extends ParentService<JsdTotalInfoDo, Long>{

    int saveAll(List<JsdTotalInfoDo> list);

}
