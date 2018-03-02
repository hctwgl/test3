/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

/**
 * @类描述：
 * @author suweili 2017年2月4日下午3:48:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
public class AfCouponDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String name;//优惠券名字
	private BigDecimal limitAmount;//最小限制金额
	private BigDecimal amount;//优惠金额
	private String useRule;//使用须知
	private Integer totalCount;//总发放数量
	private Date gmtStart;//有效期开始时间
	private Date gmtEnd;//有效期结束时间
	private Integer validDays;//有效天数，与有效开始/结束时间互斥，要么设置有效开始时间有效结束时间，要么设置有效期  【  -1:表示永久有效;  0：表示设置了有效期;  >0:表示有效期（天数）】
	private Integer limitCount;
	private String status;	
	private String type; //优惠券类型【MOBILE：话费充值， REPAYMENT：还款, FULLVOUCHER:满减卷,CASH:现金奖励】
	private String expiryType;//有效期类型【D:days固定天数，R:range固定时间范围】
	private Integer quotaAlready;
	private Long quota;
	private String useRange;
	private String activityType;
	private Long activityId;
	private Integer isGlobal;
	private String creator;
	private String modifier;
	private String shopUrl;


	public AfCouponDo(){

	}

	public AfCouponDo(Long rid, Integer quotaAlready){
		this.rid = rid;
		this.quotaAlready = quotaAlready;
	}



}