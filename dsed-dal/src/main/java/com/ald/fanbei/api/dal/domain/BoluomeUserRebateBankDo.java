package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class BoluomeUserRebateBankDo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1922188348697976993L;
	
	private String userName;
	private BigDecimal totalRebate;
	private BigDecimal inviteRebate;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public BigDecimal getTotalRebate() {
		return totalRebate;
	}
	public void setTotalRebate(BigDecimal totalRebate) {
		this.totalRebate = totalRebate;
	}
	public BigDecimal getInviteRebate() {
		return inviteRebate;
	}
	public void setInviteRebate(BigDecimal inviteRebate) {
		this.inviteRebate = inviteRebate;
	}
	
}
