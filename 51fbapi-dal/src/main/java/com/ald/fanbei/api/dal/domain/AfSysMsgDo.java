package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 *@类描述：AfSysMsgDo
 *@author 何鑫 2017年1月19日  20:04:51
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfSysMsgDo extends AbstractSerial{

	private static final long serialVersionUID = -7144070999321334675L;

	private Long	rid;
	
	private Date	gmtCreate;
	
	private Long 	userId;
	
	private String	title;//标题
	
	private String  content;//内容

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
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
