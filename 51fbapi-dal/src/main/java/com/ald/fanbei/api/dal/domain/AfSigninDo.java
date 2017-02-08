/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年2月7日下午1:36:44
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSigninDo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long rid;
	private Integer isDelete;
	private Date gmtCreate;
	private Date gmtSeries;
	private Long userId;
	private Long totalCount;
	private Long seriesCount;
	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}
	/**
	 * @return the isDelete
	 */
	public Integer getIsDelete() {
		return isDelete;
	}
	/**
	 * @param isDelete the isDelete to set
	 */
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}
	/**
	 * @return the gmtCreate
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * @param gmtCreate the gmtCreate to set
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	/**
	 * @return the gmtSeries
	 */
	public Date getGmtSeries() {
		return gmtSeries;
	}
	/**
	 * @param gmtSeries the gmtSeries to set
	 */
	public void setGmtSeries(Date gmtSeries) {
		this.gmtSeries = gmtSeries;
	}
	/**
	 * @return the userId
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * @return the totalCount
	 */
	public Long getTotalCount() {
		return totalCount;
	}
	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	/**
	 * @return the seriesCount
	 */
	public Long getSeriesCount() {
		return seriesCount;
	}
	/**
	 * @param seriesCount the seriesCount to set
	 */
	public void setSeriesCount(Long seriesCount) {
		this.seriesCount = seriesCount;
	}

}
