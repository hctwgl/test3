package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfIagentResultDo;
import com.ald.fanbei.api.dal.domain.dto.AfIagentResultDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 智能电核表Dao
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-03-27 16:57:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfIagentResultDao extends BaseDao<AfIagentResultDo, Long> {

    public void updateResultByWorkId(AfIagentResultDo afIagentResultDo);
    public AfIagentResultDo getIagentByWorkId(@Param("workId") long workId);

    List<AfIagentResultDo> getIagentByUserIdAndStatusTime(AfIagentResultDto resultDto);

    public AfIagentResultDo getIagentByUserIdToday(@Param("userId") long userId);

}
