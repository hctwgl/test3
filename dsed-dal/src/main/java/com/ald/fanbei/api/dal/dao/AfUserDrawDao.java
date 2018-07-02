package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserDrawDo;
import com.ald.fanbei.api.dal.domain.dto.UserDrawInfo;

/**
 * 年会抽奖Dao
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2017-12-27 16:31:00 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserDrawDao extends BaseDao<AfUserDrawDo, Long> {

    AfUserDrawDo getByPhone(String phone);

    AfUserDrawDo getByOpenId(String openId);

    List<AfUserDrawDo> getByPhoneAndStatus(@Param("phone") String phone, @Param("status") Integer status);

    List<UserDrawInfo> getByStatus(Integer status);

    int updateWinUserStatus(@Param("status") Integer status, @Param("userList") List<UserDrawInfo> userList);
}
