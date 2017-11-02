/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年2月21日下午2:23:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfEncoreGoodsDto extends AfGoodsDo {

	private static final long serialVersionUID = -3162832676757115210L;
	private Integer titleType;
	private String doubleRebate;
	
	public Integer getTitleType() {
		return titleType;
	}
	public void setTitleType(Integer titleType) {
		this.titleType = titleType;
	}
	public String getDoubleRebate() {
		return doubleRebate;
	}
	public void setDoubleRebate(String doubleRebate) {
		this.doubleRebate = doubleRebate;
	}
	
}
