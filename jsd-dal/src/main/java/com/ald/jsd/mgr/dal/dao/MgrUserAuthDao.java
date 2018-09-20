package com.ald.jsd.mgr.dal.dao;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.JsdUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.UserAuthDto;
import com.ald.fanbei.api.dal.query.UserAuthQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Dao
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrUserAuthDao extends BaseDao<JsdUserAuthDo, Long> {

    List<UserAuthDto> getListUserAuth(UserAuthQuery query);

    int getPassPersonNumByStatusAndDays(@Param("status") String status,@Param("days") Integer days);

    JsdUserAuthDo getByUserId(@Param("userId") Long userId);

    int getSubmitPersonNum();

    int getPassPersonNum();
}
