package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfVisualH5Do;

/**
 * 可视化H5Service
 * 
 * @author 周锐
 * @version 1.0.0 初始化
 * @date 2018-04-09 11:02:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfVisualH5Service extends ParentService<AfVisualH5Do, Long>{

    int updateById(AfVisualH5Do afVisualH5Do);

    AfVisualH5Do getById(Long id);

}
