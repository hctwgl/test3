package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @author luoxiao 2018年3月16日 下午1:25:09
 * @类描述：商圈订单结算订单实体
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 */
public class AfTradeSettleOrderDo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 结算订单ID
	 */
	private Long id;

	/**
	 * 结算订单明细，格式：XX（租房、线下商圈）结算订单-用户名-批次X
	 */
	private String details;

	/**
	 * 商户ID
	 */
	private Long businessId;

	/**
	 * 商户名称
	 */
	private String businessName;

	/**
	 * 订单状态： 待提现：EXTRACTABLE 已提现：EXTRACTED 提现中：EXTRACTING 不可提现：NOT_EXTRACTABLE
	 * 取消结算：CANCER
	 */
	private String status;

	/**
	 * 结算金额
	 */
	private BigDecimal balanceAmount;

	/**
	 * 分期月份数
	 */
	private Integer batchMonth;

	/**
	 * 分期提现延期天数
	 */
	private Integer batchDelayDays;

	/**
	 * 可提现时间
	 */
	private Date extractableDate;

	/**
	 * 提现时间
	 */
	private Date extractedDate;

	/**
	 * 订单ID
	 */
	private Long orderId;

	/**
	 * 订单号
	 */
	private String orderNo;

	/**
	 * 是否删除： 0：有效；1：删除
	 */
	private Integer isDelete;

	/**
	 * 创建人
	 */
	private String creator;

	/**
	 * 修改人
	 */
	private String modifier;

	/**
	 * 创建日期
	 */
	private Date gmtCreate;

	/**
	 * 修改日期
	 */
	private Date gmtModified;

	// 时间前台展示
	private String extractableDateFormat;
	private String extractedDateFormat;
	private String createDateFormat;
	private String updateDateFormat;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Long getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Long businessId) {
		this.businessId = businessId;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public Integer getBatchMonth() {
		return batchMonth;
	}

	public void setBatchMonth(Integer batchMonth) {
		this.batchMonth = batchMonth;
	}

	public Integer getBatchDelayDays() {
		return batchDelayDays;
	}

	public void setBatchDelayDays(Integer batchDelayDays) {
		this.batchDelayDays = batchDelayDays;
	}

	public Date getExtractableDate() {
		return extractableDate;
	}

	public void setExtractableDate(Date extractableDate) {
		this.extractableDate = extractableDate;
	}

	public Date getExtractedDate() {
		return extractedDate;
	}

	public void setExtractedDate(Date extractedDate) {
		this.extractedDate = extractedDate;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getExtractableDateFormat() {
		if (this.getExtractableDate() == null) {
			extractableDateFormat = null;
		} else {
			extractableDateFormat = simpleDateFormat.format(this
					.getExtractableDate());
		}

		return extractableDateFormat;
	}

	public String getExtractedDateFormat() {
		if (this.getExtractedDate() == null) {
			extractedDateFormat = null;
		} else {
			extractedDateFormat = simpleDateFormat.format(this.getExtractedDate());
		}

		return extractedDateFormat;
	}

	public String getCreateDateFormat() {
		if (this.getGmtCreate()== null) {
			createDateFormat = null;
		} else {
			createDateFormat = simpleDateFormat.format(this.getGmtCreate());
		}

		return createDateFormat;
	}

	public String getUpdateDateFormat() {
		if (this.getGmtModified() == null) {
			updateDateFormat = null;
		} else {
			updateDateFormat = simpleDateFormat.format(this.getGmtModified());
		}

		return updateDateFormat;
	}
}
