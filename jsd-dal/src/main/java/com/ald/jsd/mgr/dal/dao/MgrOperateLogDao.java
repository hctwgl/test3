package com.ald.jsd.mgr.dal.dao;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.jsd.mgr.dal.domain.MgrOperateLogDo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

public interface MgrOperateLogDao extends BaseDao<MgrOperateLogDo, Long> {

    int addOperateLog(@Param("userName") String userName, @Param("operateDesc") String operateDesc);
}
