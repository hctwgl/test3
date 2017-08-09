package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfScrollbarVo extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3772859463255267974L;
	
	private String wordUrl;
	private String type;
	private String content;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWordUrl() {
		return wordUrl;
	}
	public void setWordUrl(String wordUrl) {
		this.wordUrl = wordUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	

}
