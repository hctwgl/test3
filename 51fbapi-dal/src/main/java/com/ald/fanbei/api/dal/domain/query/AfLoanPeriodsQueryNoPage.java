package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author wangli
 * @date 2018/4/14 12:41
 */
@Getter
@Setter
public class AfLoanPeriodsQueryNoPage extends AfLoanPeriodsDo {

    // 还款时间开始
    private Date gmtPlanRepayStart;

    // 还款时间结束
    private Date gmtPlanRepayEnd;
}
