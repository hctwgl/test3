package com.ald.jsd.mgr.dal.dao;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 极速贷Dao
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrBorrowLegalOrderRepaymentDao extends BaseDao<JsdBorrowLegalOrderRepaymentDo, Long> {

    JsdBorrowLegalOrderRepaymentDo getByTradeNoOut(@Param("tradeNoUps") String tradeNoUps);

    List<JsdBorrowLegalOrderRepaymentDo> getBorrowCashByDays(Integer days);

}
