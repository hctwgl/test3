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
}
