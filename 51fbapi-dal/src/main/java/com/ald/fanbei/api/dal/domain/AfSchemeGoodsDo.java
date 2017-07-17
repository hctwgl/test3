/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年6月6日下午1:57:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSchemeGoodsDo extends AbstractSerial {

	private static final long serialVersionUID = -6015901235944048633L;
	
	private Long id;
	private Date gmtCreate;
	private Date gmtModified;
	private Long schemeId;
	private Long goodsId;
	private Long interestFreeId;//免息规则id
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}
	/**
	 * @param gmtModified the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	/**
	 * @return the schemeId
	 */
	public Long getSchemeId() {
		return schemeId;
	}
	/**
	 * @param schemeId the schemeId to set
	 */
	public void setSchemeId(Long schemeId) {
		this.schemeId = schemeId;
	}
	/**
	 * @return the goodsId
	 */
	public Long getGoodsId() {
		return goodsId;
	}
	/**
	 * @param goodsId the goodsId to set
	 */
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	/**
	 * @return the interestFreeId
	 */
	public Long getInterestFreeId() {
		return interestFreeId;
	}
	/**
	 * @param interestFreeId the interestFreeId to set
	 */
	public void setInterestFreeId(Long interestFreeId) {
		this.interestFreeId = interestFreeId;
	}
	

	

}
