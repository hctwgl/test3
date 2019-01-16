package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashOverdueLogDo;
import com.ald.fanbei.api.dal.domain.dto.JsdBorrowCashOverdueLogDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-09-04 11:10:19
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashOverdueLogService extends ParentService<JsdBorrowCashOverdueLogDo, Long>{

   int  getBorrowCashOverDueLogByNow(String borrowId);

   List<JsdBorrowCashOverdueLogDto> getListCashOverdueLogByBorrowId(Long borrowId, Date payTime);

   List<JsdBorrowCashOverdueLogDto> getListOrderCashOverdueLogByBorrowId(Long borrowId, Date payTime);

   List<JsdBorrowCashOverdueLogDo> getListCashOverdueLog(Date payTime);

   int getBorrowCashOverDueLogToDay();


}
