package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 
 * @类描述：电核内容信息修改
 * @author guoshuaiqiang 2018年3月29日下午4:04:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class AfBklDo extends AbstractSerial {

	private static final long serialVersionUID = 3906727774409131470L;

	private String csvPhoneNum;
	private String csvArn;
	private String csvName;
	private String csvSex;
	private String csvDigit4;
	private String csvBirthDate;
	private String csvStaging;
	private String csvAmt;
	private String csvPayWay;
	private String csvProductCategory;
	private Long orderId;
	private Long userId;
	private String iagentState;

	

}
