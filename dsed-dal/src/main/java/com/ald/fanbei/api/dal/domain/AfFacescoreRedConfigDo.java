package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 颜值测试红包配置表实体类实体
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-12 16:36:56
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfFacescoreRedConfigDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     *  颜值等级 0: 神级 1：一等级 2：二等级 3: 三等级
4：四等级 5：五等级
     */
    private String type;

    /**
     * 概率区间开始值
     */
    private Integer probabilityAreaStart;

    /**
     * 概率区间的最大值
     */
    private Integer probabilityAreaEnd;

    /**
     * 奖励金额最小值
     */
    private BigDecimal minmoney;

    /**
     * 奖励金额最大值
     */
    private BigDecimal maxmoney;
    
    /**
     * 状态：【O:Open 关闭 C:Close：开启】
     */
    private String status;

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

    /**
     * 获取 颜值等级 0: 神级 1：一等级 2：二等级 3: 三等级
4：四等级 5：五等级
     *
     * @return  颜值等级 0: 神级 1：一等级 2：二等级 3: 三等级
4：四等级 5：五等级
     */
    public String getType(){
      return type;
    }

    /**
     * 设置 颜值等级 0: 神级 1：一等级 2：二等级 3: 三等级
4：四等级 5：五等级
     * 
     * @param type 要设置的 颜值等级 0: 神级 1：一等级 2：二等级 3: 三等级
4：四等级 5：五等级
     */
    public void setType(String type){
      this.type = type;
    }

    /**
     * 获取概率区间开始值
     *
     * @return 概率区间开始值
     */
    public Integer getProbabilityAreaStart(){
      return probabilityAreaStart;
    }

    /**
     * 设置概率区间开始值
     * 
     * @param probabilityAreaStart 要设置的概率区间开始值
     */
    public void setProbabilityAreaStart(Integer probabilityAreaStart){
      this.probabilityAreaStart = probabilityAreaStart;
    }

    /**
     * 获取概率区间的最大值
     *
     * @return 概率区间的最大值
     */
    public Integer getProbabilityAreaEnd(){
      return probabilityAreaEnd;
    }

    /**
     * 设置概率区间的最大值
     * 
     * @param probabilityAreaEnd 要设置的概率区间的最大值
     */
    public void setProbabilityAreaEnd(Integer probabilityAreaEnd){
      this.probabilityAreaEnd = probabilityAreaEnd;
    }

    /**
     * 获取奖励金额最小值
     *
     * @return 奖励金额最小值
     */
    public BigDecimal getMinmoney(){
      return minmoney;
    }

    /**
     * 设置奖励金额最小值
     * 
     * @param minmoney 要设置的奖励金额最小值
     */
    public void setMinmoney(BigDecimal minmoney){
      this.minmoney = minmoney;
    }

    /**
     * 获取奖励金额最大值
     *
     * @return 奖励金额最大值
     */
    public BigDecimal getMaxmoney(){
      return maxmoney;
    }

    /**
     * 设置奖励金额最大值
     * 
     * @param maxmoney 要设置的奖励金额最大值
     */
    public void setMaxmoney(BigDecimal maxmoney){
      this.maxmoney = maxmoney;
    }

}