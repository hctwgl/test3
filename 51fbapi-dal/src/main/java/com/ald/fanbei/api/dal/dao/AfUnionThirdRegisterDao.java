package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUnionThirdRegisterDo;
import org.apache.ibatis.annotations.Param;

/**
 * '联合注册成功日志表Dao
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-10-05 10:18:05
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUnionThirdRegisterDao extends BaseDao<AfUnionThirdRegisterDo, Long> {


    int getIsRegister(@Param("phone")String phone,@Param("lsmNo")String lsmNo);
}
