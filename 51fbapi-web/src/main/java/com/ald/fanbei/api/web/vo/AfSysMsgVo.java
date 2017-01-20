package com.ald.fanbei.api.web.vo;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 *@类描述：AfSysMsgVo
 *@author 何鑫 2017年1月20日  09:56:35
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSysMsgVo extends AbstractSerial{

	private static final long serialVersionUID = -4929832958459279007L;

	private	Long	rid;
	
	private Date	noticeTime;
	
	private Long	userId;
	
	private String	title;
	
	private String  content;

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(Date noticeTime) {
		this.noticeTime = noticeTime;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
}
