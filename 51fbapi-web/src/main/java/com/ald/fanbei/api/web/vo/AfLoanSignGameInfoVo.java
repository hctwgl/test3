package com.ald.fanbei.api.web.vo;

import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 借贷超市签到游戏配置
 * @类描述:
 *
 * @auther caihuan 2017年9月13日
 * @注意:本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfLoanSignGameInfoVo extends AbstractSerial {
	
	private Long rid; 
	
	private int days; //签到天数
	
	private String name; //名称，xx天
	
	private boolean complete; //是否完成
	
	private boolean canReceive; //是否可以领取

	private List<String> couponNames; //优惠券列表

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCouponNames() {
		return couponNames;
	}

	public void setCouponNames(List<String> couponNames) {
		this.couponNames = couponNames;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public boolean isCanReceive() {
		return canReceive;
	}

	public void setCanReceive(boolean canReceive) {
		this.canReceive = canReceive;
	}
}
