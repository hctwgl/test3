package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.dto.JsdCashDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBorrowCashOverdueInfoDto;
import com.ald.fanbei.api.dal.domain.dto.LoanDto;
import com.ald.fanbei.api.dal.query.LoanQuery;
import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;
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

    JsdBorrowCashDo getByRenewalNo(@Param("renewalNo") String renewalNo);

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


    /*-------------------------------------------------------管理后台------------------------------------------------------------------------------*/

    /**
     * 借款审批列表
     *
     * @param query
     * @return
     */
    List<LoanDto> getReviewLoanList(LoanQuery query);

    /**
     * 借款审批统计
     *
     * @return
     */
    HashMap<String, Long> getReviewLoanStatistics();

    /**
     * 借款统计
     *
     * @return
     */
    HashMap<String, Object> getLoanStatistics();

    /**
     * 还款计划统计
     *
     * @return
     */
    HashMap<String, Object> getRepayStatistics();

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

    /**
     * 代扣数
     * @param todayLast
     * @return
     */
    int getBorrowCashByBeforeTodayCount(@Param("todayLast") Date todayLast);

    /**
     * 代扣数据
     * @param nowPage
     * @param pageSize
     * @param todayLast
     * @return
     */
    List<JsdBorrowCashDo> getBorrowCashByBeforeToday(@Param("beginIndex")int nowPage, @Param("pageSize")int pageSize, @Param("todayLast")Date todayLast);

    /**
     * 借款列表
     *
     * @param query
     * @return
     */
    List<LoanDto> getLoanList(LoanQuery query);

    /**
     * 还款计划列表
     *
     * @param query
     * @return
     */
    List<LoanDto> getRepayList(LoanQuery query);

    /*-------------------------------------------------------第三方接口------------------------------------------------------------------------------*/

    List<JsdBorrowCashDo> getTransedCashDtosByUserId(@Param("userId") Long userId);

    JsdBorrowCashDo getLastFinishCashByUserId(@Param("userId") Long userId);

    JsdCashDto getGoodsInfoByBorrowId(@Param("borrowId") Long borrowId);

    /**
     *获取还款日在当前数据
     * @param userIds
     * @param todayLast
     * @return
     */
    List<JsdBorrowCashDo> getBorrowCashRepayByUserIds(@Param("userIds")String userIds,@Param("todayLast") Date todayLast);

    /**
     * 获取结算系统实付数据
     * @Param list {@link FinaneceDataDo} 对象
     *@return  <code>List<code/>
     *
     * **/
    List<FinaneceDataDo> getPaymentDetail();


    /**
     * 获取结算系统应收数据
     * @Param list {@link FinaneceDataDo} 对象
     *@return  <code>List<code/>
     *
     * **/
    List<FinaneceDataDo> getPromiseIncomeDetail();


    AfUserBorrowCashOverdueInfoDto getOverdueInfoByUserId(@Param("userId") Long userId);

    JsdBorrowCashDo getBorrowById(@Param("id")Long id);

    JsdBorrowCashDo getBorrowByRid(@Param("id")Long id);

    /**
     * 获取逾期当天数据量
     * @param
     * @return
     */
    int getBorrowCashByOverdueCountBySection(@Param("startOverdue")Date startOverdue, @Param("endOverdue")Date endOverdue);

    /**
     * 获取逾期当天数据
     * @param startTime
     * @param endTime
     * @return
     */
    List<JsdBorrowCashDo> getBorrowCashOverdueBySection( @Param("startTime")Date startTime,@Param("endTime") Date endTime);

    /**
     * 获取逾期当天数据量
     * @param todayLast
     * @return
     */
    int getBorrowCashByTodayCount(@Param("todayLast") String todayLast);

    /**
     * 获取还款日当天数据
     * @param todayLast
     * @return
     */
    List<JsdBorrowCashDo> getBorrowCashByToday(@Param("todayLast")String todayLast);

    List<JsdBorrowCashDo> getTodayBorrowCashRepayByUserIds(@Param("userIds")String userIds, @Param("todayLast")String todayLast);

    List<JsdBorrowCashDo> getOverSectionBorrowCashRepayByUserIds(@Param("userIds")String userIds,@Param("startTime")Date startTime,@Param("endTime")Date endTime);

    Integer getLoanNum(@Param("nper")String nper);

}
