package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 贷款业务实体
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-24 11:26:57
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfLoanRateDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

   private String interestRate;

   private String poundageRate;

   private String overdueRate;

   private String borrowTag;

   public String getInterestRate() {
      return interestRate;
   }

   public void setInterestRate(String interestRate) {
      this.interestRate = interestRate;
   }

   public String getPoundageRate() {
      return poundageRate;
   }

   public void setPoundageRate(String poundageRate) {
      this.poundageRate = poundageRate;
   }

   public String getOverdueRate() {
      return overdueRate;
   }

   public void setOverdueRate(String overdueRate) {
      this.overdueRate = overdueRate;
   }

   public String getBorrowTag() {
      return borrowTag;
   }

   public void setBorrowTag(String borrowTag) {
      this.borrowTag = borrowTag;
   }
}