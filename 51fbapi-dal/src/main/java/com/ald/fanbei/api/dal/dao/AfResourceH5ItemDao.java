package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * h5商品资源管理Dao
 *
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:41:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceH5ItemDao extends BaseDao<AfResourceH5ItemDo, Long> {
	List<AfResourceH5ItemDto> selectByModelId(@Param("modelId") Long modelId);
}
