package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfAlipayOfflineTradeNoDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 支付宝线下转账流水号Dao
 * 
 * @author xieqiang
 * @version 1.0.0 初始化
 * @date 2018-03-22 16:42:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAlipayOfflineTradeNoDao extends BaseDao<AfAlipayOfflineTradeNoDo, Long> {

    public List<Map<String,Object>> getTradeNosByUserId(@Param("userId") long userId, @Param("status") String status);

}
