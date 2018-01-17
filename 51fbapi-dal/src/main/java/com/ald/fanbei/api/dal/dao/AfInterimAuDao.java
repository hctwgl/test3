package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfInterimAuDo;
import com.ald.fanbei.api.dal.domain.AfInterimAuLogDo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhourui on 2017年11月17日 14:52
 * @类描述：用户临时额度
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfInterimAuDao {
    /**
     * 增加记录
     * @param afInterimAuDo
     * @return
     */
    int addInterimAu(AfInterimAuDo afInterimAuDo);

    /**
     * 通过userId查询
     * @param userId
     * @return
     */
    AfInterimAuDo getByUserId(Long userId);

    /**
     * 修改用户已使用额度
     * @param userId 用户id
     * @param usedAmount 使用额度【正数加已使用额度，负数减已使用额度】
     * @return
     */
    int updateInterimUsed(@Param("userId") Long userId, @Param("usedAmount") BigDecimal usedAmount);

    /**
     * 查询用户是否已经提额过
     */
    int selectExistAuByUserId(Long userId);
    /**
     * 查询提额的额度
     */
    BigDecimal selectAuByUserId(Long userId);

    /**
     * 查询申请失败记录
     */
    List<AfInterimAuDo> selectApplyFailByUserId(Long userId);

    /**
     * 插入af_interim_au
     * @param userId
     * @param interimAmount
     * @param gmtFailuretime
     * @return
     */
    int insertInterimAmount(@Param("userId") Long userId, @Param("interimAmount") BigDecimal interimAmount, @Param("gmtFailuretime") Date gmtFailuretime);

    /**
     * 插入af_interim_au_log
     * @param userId
     * @param interimAmount
     * @param gmtFailuretime
     * @param status
     * @return
     */
    int insertInterimAmountLog(@Param("userId") Long userId, @Param("interimAmount") BigDecimal interimAmount, @Param("gmtFailuretime") Date gmtFailuretime, @Param("status") int status);

    /**
     * 查询是否已失效的提额
     * @param userId
     * @return
     */
    int getFailureCount(@Param("userId") Long userId);

    /**
     * 更新af_interim_au
     * @param userId
     * @param interimAmount
     * @param gmtFailuretime
     * @return
     */
    int updateInterimAmount(@Param("userId") Long userId, @Param("interimAmount") BigDecimal interimAmount, @Param("gmtFailuretime") Date gmtFailuretime);


    /**
     * 获取最后一次提升临时额度记录
     */
    AfInterimAuLogDo getLastLogByUserId(Long userId);

    /**
     * 现金贷最小还款时间至今天数
     */
    Integer getLoanMinRepayDay(Long userId);

    /**
     * 消费分期最小还款时间至今天数
     */
    Integer getConsumeMinRepayDay(Long userId);

    /**
     * 现金贷续借次数
     */
    Integer getLoanRenewCount(Long userId);

    /**
     * 发生当天借款当天还款行为次数
     */
    Integer getBorrowRepayCount(Long userId);

    /**
     * 累计分期次数 累计分期次数
     */
    HashMap getSumConsume(Long userId);

    /**
     * 累计现金贷次数 累计现金贷金额
     */
    HashMap getSumLoan(Long userId);

    /**
     * 消费分期已出账单数
     */
    Integer getConsumeBillCount(Long userId);
}
