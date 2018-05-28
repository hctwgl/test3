package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.AfAdvertiseDo;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 定向广告规则实体
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-05-17 22:38:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfAdvertiseDto extends AfAdvertiseDo {

   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
     private Integer appendMode;

	public Integer getAppendMode() {
		return appendMode;
	}
	
	public void setAppendMode(Integer appendMode) {
		this.appendMode = appendMode;
	}
   

}