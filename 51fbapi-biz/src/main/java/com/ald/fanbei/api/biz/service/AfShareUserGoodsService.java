package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfShareUserGoodsDo;

/**
 * 新人专享Service
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-11-02 11:16:29
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfShareUserGoodsService extends ParentService<AfShareUserGoodsDo, Long>{

	void updateIsBuyById(long parseLong, int i);

	AfShareUserGoodsDo getByUserId(Long userId);

}
