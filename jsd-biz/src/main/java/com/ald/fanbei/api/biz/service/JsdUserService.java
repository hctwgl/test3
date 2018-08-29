package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdUserDo;

/**
 * 极速贷用户信息Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:43:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdUserService extends ParentService<JsdUserDo, Long>{

	JsdUserDo getByOpenId(String openId);

}
