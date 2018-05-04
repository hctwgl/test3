package com.ald.fanbei.api.biz.service;


import com.ald.fanbei.api.dal.domain.AfResourceH5Do;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;

import java.util.List;

/**
 * h5资源管理
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:39:09 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceH5Service {

	List<AfResourceH5Dto> selectByStatus(String tag);

	/**
	 * 根据页面标识获H5资源
	 *
	 * @author wangli
	 * @date 2018/4/10 20:01
	 */
	AfResourceH5Dto getByPageFlag(String lifePageFlag);
}
