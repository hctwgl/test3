package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdEdspayUserInfoDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Dao
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-11-15 18:37:18
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdEdspayUserInfoDao extends BaseDao<JsdEdspayUserInfoDo, Long> {

    List<JsdEdspayUserInfoDo> getInfoByTypeAndTypeId(@Param(value = "type") Byte type, @Param(value = "typeId") Long typeId);
    

}
