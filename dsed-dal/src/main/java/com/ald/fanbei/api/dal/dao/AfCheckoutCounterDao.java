package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfCheckoutCounterDo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 *  收银台相关配置表Dao
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-10-16 09:46:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfCheckoutCounterDao extends BaseDao<AfCheckoutCounterDo, Long> {

    AfCheckoutCounterDo getByType(@Param("type") String type,@Param("secType")  String secType);
    Integer countFinishBorrowCash(@Param("userId") Long userId);
    Integer sumOverdueDayBorrowCash(@Param("userId") Long userId);
    BigDecimal sumBorrowAmount(@Param("userId") Long userId);
    Integer countBorrow(@Param("userId") Long userId);
    Integer sumOverdueDayBorrow(@Param("userId") Long userId);

}
