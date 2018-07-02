package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserToutiaoDo;

/**
 * '第三方-上树请求记录Service
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-05 16:39:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserToutiaoService {

    int creatUser(AfUserToutiaoDo afUserToutiaoDo);

    int uptUser(Long id);

    AfUserToutiaoDo getUser(String imei, String androidid, String idfa);

    Long uptUserActive(Long rid,Long userIdToutiao, String userNameToutiao);

    AfUserToutiaoDo getUserActive(String imeiMd5, String androidId, String idfa);

    AfUserToutiaoDo getUserOpen(String imeiMd5, String androidId, String idfa);

    Long uptUserOpen(Long rid, Long userIdToutiao, String userNameToutiao);
}
