package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.dto.ReviewLoanDto;
import com.ald.fanbei.api.dal.query.ReviewLoanQuery;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 极速贷Dao
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdBorrowCashDao extends BaseDao<JsdBorrowCashDo, Long> {

    String getCurrentLastBorrowNo(String orderNoPre);

    JsdBorrowCashDo getByBorrowNo(@Param("borrowNo") String borrowNo);

    JsdBorrowCashDo getByTradeNoXgxy(@Param("tradeNoXgxy") String tradeNoXgxy);

    /**
     * 获取不在finish和closed状态的借款
     *
     * @param userId
     * @return
     */
    List<JsdBorrowCashDo> getBorrowCashByStatusNotInFinshAndClosed(@Param("userId") Long userId);

    /**
     * 获取当前逾期数据的数量
     *
     * @param nowTime
     * @return
     */
    int getBorrowCashOverdueCount(@Param("nowTime") Date nowTime);


    /**
     * 分页获取某个时间点的逾期借款
     */
    List<JsdBorrowCashDo> getBorrowCashOverduePaging(@Param("nowTime") Date nowTime, @Param("beginIndex") int beginIndex, @Param("pageSize") int pageSize);

    List<JsdBorrowCashDo> getBorrowCashOverdueByUserIds(@Param("nowTime") Date nowTime, @Param("userIds") String userIds);

    int updateReviewStatus(@Param("reviewStatus") String reviewStatus, @Param("borrowId") Long borrowId);

    BigDecimal getCurrDayAllamount();


    /*-------------------------------------------------------管理后台------------------------------------------------------------------------------*/

    /**
     * 借款审批列表
     *
     * @param query
     * @return
     */
    List<ReviewLoanDto> getReviewLoanList(ReviewLoanQuery query);

    /**
     * 借款审批统计
     *
     * @return
     */
    HashMap<String, Long> getReviewLoanStatistics();

    /**
     * 借款审批-拒绝
     *
     * @param reviewRemark
     * @param tradeNoXgxy
     * @return
     */
    int refuseByXgNo(@Param("reviewRemark") String reviewRemark, @Param("tradeNoXgxy") String tradeNoXgxy);

    /**
     * 借款审批-通过
     *
     * @param tradeNoXgxy
     * @return
     */
    int passByXgNo(@Param("tradeNoXgxy") String tradeNoXgxy);
}
