package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：用户账号相关信息
 * @author fumeiai 2017年4月19日下午4:35:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfContactsOldDo extends AbstractSerial {

	private static final long serialVersionUID = 570354935460086063L;

	private Long id;
	private Long uid;
	private String mobileBook;
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getMobileBook() {
		return mobileBook;
	}

	public void setMobileBook(String mobileBook) {
		this.mobileBook = mobileBook;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "AfContactsOldDo [id=" + id + ", uid=" + uid + ", mobileBook=" + mobileBook + ", createTime=" + createTime + "]";
	}

}
