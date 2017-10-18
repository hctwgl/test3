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

    int uptUser(@Param("id")Long id);

    AfUserToutiaoDo getUser(@Param("imei")String imei, @Param("androidid1")String androidid1, @Param("idfa")String idfa);

    Long uptUserActive(@Param("id")Long id,@Param("userIdToutiao")Long userIdToutiao, @Param("userNameToutiao")String userNameToutiao);

    AfUserToutiaoDo getUserActive(@Param("imei")String imei, @Param("androidid1")String androidid1, @Param("idfa")String idfa);

    AfUserToutiaoDo getUserOpen(@Param("imei")String imei, @Param("androidid1")String androidid1, @Param("idfa")String idfa);

    Long uptUserOpen(@Param("id")Long rid, @Param("userIdToutiao")Long userIdToutiao, @Param("userNameToutiao")String userNameToutiao);
}
