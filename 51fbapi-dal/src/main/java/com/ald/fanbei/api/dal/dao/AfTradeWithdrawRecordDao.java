package com.ald.fanbei.api.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfTradeWithdrawRecordDo;

/**
 * 商圈商户提现记录表Dao
 *
 * @author huyang
 * @version 1.0.0 初始化
 * @date 2017-07-14 16:46:58
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTradeWithdrawRecordDao extends BaseDao<AfTradeWithdrawRecordDo, Long> {

    /**
     * 提现明细时间列表
     *
     * @param businessId
     * @param startDate
     * @param endDate
     * @return
     */
    List<String> withdrawGridDate(@Param("businessId") Long businessId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 分页查询商圈提现记录
     *
     * @param businessId
     * @param startDate
     * @param endDate
     * @return
     */
    List<AfTradeWithdrawRecordDo> withdrawGrid(@Param("businessId") Long businessId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
