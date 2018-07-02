package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfCategoryOprationDo;

/**
 * 分类运营位配置Dao
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-04-11 19:59:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCategoryOprationDao extends BaseDao<AfCategoryOprationDo, Long> {
	/**
	 * 通过一级类目ID获取分类运营位配置信息
	 * @param cateGoryId
	 * @return
	 */
	AfCategoryOprationDo getByCategoryId(Long cateGoryId);

    

}
