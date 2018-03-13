package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 贷款业务实体
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-23 13:41:22
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfLoanProductDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 产品名称
     */
    private String name;

    /**
     * 最低限制金额
     */
    private BigDecimal minAmount;

    /**
     * 最高限制金额
     */
    private BigDecimal maxAmount;

    /**
     * 产品说明
     */
    private String desz;

    /**
     * 产品参数配置
     */
    private String conf;

    /**
     * 产品可借期数
     */
    private Integer periods;

    /**
     * 产品标识
     */
    private String prdType;

    /**
     * 开关
     */
    private String switch_;
    
    /**
     * 到期还款提醒天数
     */
    private Integer remindDay;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;



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
     * 获取产品名称
     *
     * @return 产品名称
     */
    public String getName(){
      return name;
    }

    /**
     * 设置产品名称
     * 
     * @param name 要设置的产品名称
     */
    public void setName(String name){
      this.name = name;
    }

    /**
     * 获取最低限制金额
     *
     * @return 最低限制金额
     */
    public BigDecimal getMinAmount(){
      return minAmount;
    }

    /**
     * 设置最低限制金额
     * 
     * @param minAmount 要设置的最低限制金额
     */
    public void setMinAmount(BigDecimal minAmount){
      this.minAmount = minAmount;
    }

    /**
     * 获取最高限制金额
     *
     * @return 最高限制金额
     */
    public BigDecimal getMaxAmount(){
      return maxAmount;
    }

    /**
     * 设置最高限制金额
     * 
     * @param maxAmount 要设置的最高限制金额
     */
    public void setMaxAmount(BigDecimal maxAmount){
      this.maxAmount = maxAmount;
    }

    /**
     * 获取产品说明
     *
     * @return 产品说明
     */
    public String getDesz(){
      return desz;
    }

    /**
     * 设置产品说明
     * 
     * @param desc 要设置的产品说明
     */
    public void setDesz(String desz){
      this.desz = desz;
    }

    /**
     * 获取产品参数配置
     *
     * @return 产品参数配置
     */
    public String getConf(){
      return conf;
    }

    /**
     * 设置产品参数配置
     * 
     * @param conf 要设置的产品参数配置
     */
    public void setConf(String conf){
      this.conf = conf;
    }

    /**
     * 获取产品可借期数
     *
     * @return 产品可借期数
     */
    public Integer getPeriods(){
      return periods;
    }

    /**
     * 设置产品可借期数
     * 
     * @param periods 要设置的产品可借期数
     */
    public void setPeriods(Integer periods){
      this.periods = periods;
    }

    /**
     * 获取产品标识
     *
     * @return 产品标识
     */
    public String getPrdType(){
      return prdType;
    }

    /**
     * 设置产品标识
     * 
     * @param prdType 要设置的产品标识
     */
    public void setPrdType(String prdType){
      this.prdType = prdType;
    }

    /**
     * 获取到期还款提醒天数
     *
     * @return 到期还款提醒天数
     */
    public Integer getRemindDay(){
      return remindDay;
    }

    /**
     * 设置到期还款提醒天数
     * 
     * @param remindDay 要设置的到期还款提醒天数
     */
    public void setRemindDay(Integer remindDay){
      this.remindDay = remindDay;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间
     * 
     * @param gmtCreate 要设置的创建时间
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置最后修改时间
     * 
     * @param gmtModified 要设置的最后修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

	public String getSwitch() {
		return switch_;
	}

	public void setSwitch(String switch_) {
		this.switch_ = switch_;
	}


}