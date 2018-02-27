/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 *@类描述：有得卖 回收业务 系统系数
 *@author weiqingeng 2018年2月27日  09:47:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRecycleRatioDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private Integer id;//自增id
	
	private Integer isDelete;//'是否删除 1：是 0：否'
	
	private Date gmtCreate;
	
	private Date gmtModified;
	
	private Integer type;//类型 1：返现系数 2：提现翻倍系数
	
	private BigDecimal ratio;//比例
	
	private Integer isDesabled;//是否失效 1：是 0：否	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getRatio() {
		return ratio;
	}

	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}

	public Integer getIsDesabled() {
		return isDesabled;
	}

	public void setIsDesabled(Integer isDesabled) {
		this.isDesabled = isDesabled;
	}
	 
}