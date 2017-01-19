/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.AfUserDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午7:52:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UserVo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private Long rid;
	private String avata;
	private String mobile;
	private String realName;
	private String nick;
	private String userName;
	
	public UserVo() {
		
	}
	
	public UserVo(AfUserDo afUserDo){
		this.setRid(afUserDo.getRid());
		this.setUserName(afUserDo.getUserName());
		this.setNick(afUserDo.getNick());
		this.setAvata(afUserDo.getAvata());
		this.setRealName(afUserDo.getRealName());
		this.setMobile(afUserDo.getMobile());
	}
	
	public String getAvata() {
		return avata;
	}
	public void setAvata(String avata) {
		this.avata = avata;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}
	
	

}
