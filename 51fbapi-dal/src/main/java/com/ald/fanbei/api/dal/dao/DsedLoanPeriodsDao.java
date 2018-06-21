package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLoanPeriodsDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;

/**
 * 都市易贷借款期数表Dao
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:44:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedLoanPeriodsDao extends BaseDao<DsedLoanPeriodsDo, Long> {

    List<DsedLoanPeriodsDto> getLoanOverdue(@Param("nowTime")Date nowTime,@Param("beginIndex")int nowPage, @Param("pageSize")int pageSize);
    /**
     * 获取逾期数据的数量
     *
     * @return
     */
    int getLoanOverdueCount(@Param("nowTime") Date nowTime);
    /**
     * 通过编号查询借款信息
     * @param loanNo
     * @author
     * @returnchefeipeng
     */
    DsedLoanPeriodsDo getLoanPeriodsByLoanNoAndNper(@Param("loanNo") String loanNo,@Param("nper")int nper);

    List<DsedLoanPeriodsDo> listByLoanId(Long loanId);

    DsedLoanPeriodsDo getLoanPeriodsByLoanNo(@Param("loanNo") String loanNo);


    List<DsedLoanPeriodsDo> getNoRepayListByLoanId(@Param("rid") Long rid);

    List<DsedLoanPeriodsDo> getNoRepayListByLoanNoAndUserId(@Param("loanNo") String loanNo,@Param("userId") Long userId);

    DsedLoanPeriodsDo getLastActivePeriodByLoanId(Long loanId);

    DsedLoanPeriodsDo getPeriodByLoanIdAndNper(@Param("loanId")Long loanId, @Param("nper")Integer nper);

}
