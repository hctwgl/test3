package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfEdspayUserInfoDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 钱包出借用户查询表Dao
 * 
 * @author gsq
 * @version 1.0.0 初始化
 * @date 2018-04-18 11:50:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfEdspayUserInfoDao extends BaseDao<AfEdspayUserInfoDo, Long> {

    List<AfEdspayUserInfoDo> getInfoByTypeAndTypeId(@Param(value = "type")Byte type,@Param(value = "typeId")Long typeId);

}
