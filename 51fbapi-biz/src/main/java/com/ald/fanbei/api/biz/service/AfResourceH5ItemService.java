package com.ald.fanbei.api.biz.service;


import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;

import java.util.List;

/**
 * h5商品资源管理Service
 *
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:41:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceH5ItemService {

	List<AfResourceH5ItemDto> selectByModelId(Long modelId);

	AfResourceH5ItemDo getByTagAndType(String tag, String type);

	List<AfResourceH5ItemDo> getByTag(String tag);

	/**
	 * 根据页面标签和排序查找
	 *
	 * @author wangli
	 * @date 2018/4/11 14:00
	 */
	List<AfResourceH5ItemDo> findListByModelTagAndSort(String tag, Integer sort);

	List<AfResourceH5ItemDo> getByTagAndValue2(String tag,
			String type);

}
