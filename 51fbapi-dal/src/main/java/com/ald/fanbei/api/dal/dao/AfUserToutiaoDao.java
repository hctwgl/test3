package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserToutiaoDo;
import org.apache.ibatis.annotations.Param;

/**
 * '第三方-上树请求记录Dao
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-05 16:39:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserToutiaoDao extends BaseDao<AfUserToutiaoDo, Long> {

    int creatUser(AfUserToutiaoDo afUserToutiaoDo);

    int getUserCount(@Param("imei")String imei, @Param("mac")String mac);

    int getUserActive(@Param("imei")String imei, @Param("mac")String mac);
}
