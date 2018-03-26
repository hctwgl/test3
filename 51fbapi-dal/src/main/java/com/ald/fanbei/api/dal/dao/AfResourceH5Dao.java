package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfResourceH5Do;

import java.util.List;

/**
 * h5资源管理Dao
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:42:22 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceH5Dao extends BaseDao<AfResourceH5Do, Long> {

	List<AfResourceH5Do> selectByStatus();

}
