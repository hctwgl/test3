package com.ald.fanbei.api.dal.domain.query;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
/**
 * 资产债权视图查询
 * @author chengkang
 */
public class AfViewAssetBorrowCashQuery implements Serializable{
	
	private static final long serialVersionUID = 3676032305548912472L;

	 /**
     * 借款类型【SEVEN:7天,FOURTEEN】
     */
    private String type;

    /**
     * 借款起始时间
     */
    private Date gmtCreateStart;
    
    /**
     * 借款截止时间
     */
    private Date gmtCreateEnd;
    
    /**
     * 最小借款id,筛选时条件中会>当前传递的borrowCashId
     */
    private Long minBorrowCashId;
    
    /**
     * 取的条数
     */
    private Integer limitNums;
    
    /**
     * 取的条数
     */
    private List<Long> userIds;


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getGmtCreateStart() {
		return gmtCreateStart;
	}

	public void setGmtCreateStart(Date gmtCreateStart) {
		this.gmtCreateStart = gmtCreateStart;
	}

	public Date getGmtCreateEnd() {
		return gmtCreateEnd;
	}

	public void setGmtCreateEnd(Date gmtCreateEnd) {
		this.gmtCreateEnd = gmtCreateEnd;
	}

	public Integer getLimitNums() {
		return limitNums;
	}

	public void setLimitNums(Integer limitNums) {
		this.limitNums = limitNums;
	}

	public Long getMinBorrowCashId() {
		return minBorrowCashId;
	}

	public void setMinBorrowCashId(Long minBorrowCashId) {
		this.minBorrowCashId = minBorrowCashId;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}
    
	
}
