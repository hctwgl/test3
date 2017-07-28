package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfTradeBusinessDo;
import org.apache.ibatis.annotations.Param;

/**
 * 商圈商户表Dao
 * 
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:40:39 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeBusinessDao extends BaseDao<AfTradeBusinessDo, Long> {

	AfTradeBusinessDo getByName(@Param("name") String username);

}
