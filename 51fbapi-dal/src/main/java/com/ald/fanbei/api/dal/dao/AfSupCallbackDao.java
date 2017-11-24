package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfSupCallbackDo;

/**
 * 游戏充值Dao
 * 
 * @author 高继斌_temple
 * @version 1.0.0 初始化
 * @date 2017-11-24 16:09:31 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSupCallbackDao extends BaseDao<AfSupCallbackDo, Long> {

    AfSupCallbackDo getByOrderNo(String orderNo);

}
