package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 颜值测试红包分享次数记录实体类实体
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-19 09:39:51
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfFacescoreShareCountDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    private Long userId;

    public AfFacescoreShareCountDo() {
		super();
	}

	public AfFacescoreShareCountDo(Long userId, Integer count) {
		super();
		this.userId = userId;
		this.count = count;
	}

	/**
     * 
     */
    private Integer count;


    /**
     * 获取
     *
     * @return 
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置
     * 
     * @param userId 要设置的
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取
     *
     * @return 
     */
    public Integer getCount(){
      return count;
    }

    /**
     * 设置
     * 
     * @param count 要设置的
     */
    public void setCount(Integer count){
      this.count = count;
    }

}