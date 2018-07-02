/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfContractPdfEdspaySealDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;

import java.math.BigDecimal;

/**
 * 
 * @类描述：
 * @author 江荣波 2017年6月21日下午2:23:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfContractPdfEdspaySealDto extends AfContractPdfEdspaySealDo {

	private static final long serialVersionUID = -1;

	private String userName;

	private String edspayUserCardId;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEdspayUserCardId() {
		return edspayUserCardId;
	}

	public void setEdspayUserCardId(String edspayUserCardId) {
		this.edspayUserCardId = edspayUserCardId;
	}
}
