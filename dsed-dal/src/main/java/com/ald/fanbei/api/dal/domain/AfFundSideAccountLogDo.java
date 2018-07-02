package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 资金方账户变动表实体
 * 
 * @author chegnkang
 * @version 1.0.0 初始化
 * @date 2017-09-29 13:54:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfFundSideAccountLogDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 资金方id
     */
    private Long fundSideInfoId;

    /**
     * 修改之前的金额
     */
    private BigDecimal amountOld;

    /**
     * 增加金额(可为负数)
     */
    private BigDecimal amountAdded;

    /**
     * 修改类型(充值：recharge  提现：withdraw  放款：loan 回款：collect)
     */
    private String changeType;

    /**
     * 日志关联的业务表id
     */
    private Long refBusiId;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 关联业务记录对应的业务编号
     */
    private String refBusiNo;
    
    /**
     * 账户变动说明
     */
    private String remarks;

    public AfFundSideAccountLogDo() {
		super();
	}

	public AfFundSideAccountLogDo(Long fundSideInfoId, BigDecimal amountOld,
			BigDecimal amountAdded, String changeType, Long refBusiId,
			Date gmtModified, String refBusiNo,String remarks) {
		super();
		this.fundSideInfoId = fundSideInfoId;
		this.amountOld = amountOld;
		this.amountAdded = amountAdded;
		this.changeType = changeType;
		this.refBusiId = refBusiId;
		this.gmtModified = gmtModified;
		this.refBusiNo = refBusiNo;
		this.remarks = remarks;
	}

	/**
     * 获取主键Id
     *
     * @return rid
     */
    public Long getRid(){
      return rid;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    
    /**
     * 获取资金方id
     *
     * @return 资金方id
     */
    public Long getFundSideInfoId(){
      return fundSideInfoId;
    }

    /**
     * 设置资金方id
     * 
     * @param fundSideInfoId 要设置的资金方id
     */
    public void setFundSideInfoId(Long fundSideInfoId){
      this.fundSideInfoId = fundSideInfoId;
    }

    /**
     * 获取修改之前的金额
     *
     * @return 修改之前的金额
     */
    public BigDecimal getAmountOld(){
      return amountOld;
    }

    /**
     * 设置修改之前的金额
     * 
     * @param amountOld 要设置的修改之前的金额
     */
    public void setAmountOld(BigDecimal amountOld){
      this.amountOld = amountOld;
    }

    /**
     * 获取增加金额(可为负数)
     *
     * @return 增加金额(可为负数)
     */
    public BigDecimal getAmountAdded(){
      return amountAdded;
    }

    /**
     * 设置增加金额(可为负数)
     * 
     * @param amountAdded 要设置的增加金额(可为负数)
     */
    public void setAmountAdded(BigDecimal amountAdded){
      this.amountAdded = amountAdded;
    }

    /**
     * 获取修改类型(充值：recharge  提现：withdraw  放款：loan 回款：collect)
     *
     * @return 修改类型(充值：recharge  提现：withdraw  放款：loan 回款：collect)
     */
    public String getChangeType(){
      return changeType;
    }

    /**
     * 设置修改类型(充值：recharge  提现：withdraw  放款：loan 回款：collect)
     * 
     * @param changeType 要设置的修改类型(充值：recharge  提现：withdraw  放款：loan 回款：collect)
     */
    public void setChangeType(String changeType){
      this.changeType = changeType;
    }

    /**
     * 获取日志关联的业务表id
     *
     * @return 日志关联的业务表id
     */
    public Long getRefBusiId(){
      return refBusiId;
    }

    /**
     * 设置日志关联的业务表id
     * 
     * @param refBusiId 要设置的日志关联的业务表id
     */
    public void setRefBusiId(Long refBusiId){
      this.refBusiId = refBusiId;
    }

    /**
     * 获取修改时间
     *
     * @return 修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置修改时间
     * 
     * @param gmtModified 要设置的修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取账户变动说明
     *
     * @return 账户变动说明
     */
    public String getRemarks(){
      return remarks;
    }

    /**
     * 设置账户变动说明
     * 
     * @param remarks 要设置的账户变动说明
     */
    public void setRemarks(String remarks){
      this.remarks = remarks;
    }

	public String getRefBusiNo() {
		return refBusiNo;
	}

	public void setRefBusiNo(String refBusiNo) {
		this.refBusiNo = refBusiNo;
	}

}