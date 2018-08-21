package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.DsedUserDo;

/**
 * 都市易贷用户信息Dao
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:34
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedUserDao extends BaseDao<DsedUserDo, Long> {

    int updateUser(DsedUserDo userDo);

    DsedUserDo getByOpenId(String openId);

	DsedUserDo getUserById(Long userId);

	DsedUserDo getUserByMobile(String mobile);

}