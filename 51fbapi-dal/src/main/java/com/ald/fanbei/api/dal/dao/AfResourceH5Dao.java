package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfResourceH5Do;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * h5资源管理Dao
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:42:22 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceH5Dao extends BaseDao<AfResourceH5Do, Long> {

	List<AfResourceH5Dto> selectByStatus(@Param("tag")String tag);

	/**
	 * 根据页面标识获取H5资源
	 *
	 * @author wangli
	 * @date 2018/4/10 20:02
	 */
    AfResourceH5Dto getByPageFlag(@Param("pageFlag") String pageFlag);
}
