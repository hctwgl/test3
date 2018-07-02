package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

/**
 * 
 *@类描述：AfUserAccountDto
 *@author 何鑫 2017年1月20日  13:27:37
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class AfUserAccountDto extends AfUserAccountDo{

	private static final long serialVersionUID = 8795694947061617207L;

	private String gender;//性别 【F：女 ，M：男， U:未知】
	private String nick;//昵称
	private String avatar;//头像
	private String realName;//真实姓名
	private String mobile;//绑定手机号
	private Integer vipLevel;//会员等级 【 1:为普通会员，2:为青铜会员，3:为白银会员，4:为黄金会员】
	private String recommendCode;//邀请码
	private String email;
	private String address;
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public Integer getVipLevel() {
		return vipLevel;
	}
	public void setVipLevel(Integer vipLevel) {
		this.vipLevel = vipLevel;
	}
	public String getRecommendCode() {
		return recommendCode;
	}
	public void setRecommendCode(String recommendCode) {
		this.recommendCode = recommendCode;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
}
