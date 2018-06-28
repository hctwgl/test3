package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedUserContactsDo;

import java.util.List;

/**
 * 都市E贷用户通讯录信息表Service
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:52:15
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedUserContactsService extends ParentService<DsedUserContactsDo, Long>{

    List<DsedUserContactsDo>  getUserContactsByUserId(String userId);

}
