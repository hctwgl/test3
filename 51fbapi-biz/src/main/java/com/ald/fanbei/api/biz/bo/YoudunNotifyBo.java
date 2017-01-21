package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 *@类描述：
 *@author 陈金虎 2017年1月20日 下午9:20:54
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
public class YoudunNotifyBo extends AbstractSerial{
	private static final long serialVersionUID = -5621553728474942232L;
	
	String frontCard;//身份证正面图片
	String backCard;//身份证反面图片
	String photoGet;//身份证证件正面的头像
	String photoGrid;//公安部返回的带网格照片
	String photoLiving;//活体清晰照
	String oidPartner; //商户编号
	String orderNo;
	String userId;
	String noProduct;
	String resultAuth;
	String signField;
	String sign;
	String beIdcard;
	String failReason;
	String idNo;
	String idName;
	String nation;
	String gender;
	String birthday;
	String age;
	String address;
	String issuingAuthority;
	String validityPeriod;
	String infoOrder;
	public String getFrontCard() {
		return frontCard;
	}
	public void setFrontCard(String frontCard) {
		this.frontCard = frontCard;
	}
	public String getBackCard() {
		return backCard;
	}
	public void setBackCard(String backCard) {
		this.backCard = backCard;
	}
	public String getPhotoGet() {
		return photoGet;
	}
	public void setPhotoGet(String photoGet) {
		this.photoGet = photoGet;
	}
	public String getPhotoGrid() {
		return photoGrid;
	}
	public void setPhotoGrid(String photoGrid) {
		this.photoGrid = photoGrid;
	}
	public String getPhotoLiving() {
		return photoLiving;
	}
	public void setPhotoLiving(String photoLiving) {
		this.photoLiving = photoLiving;
	}
	public String getOidPartner() {
		return oidPartner;
	}
	public void setOidPartner(String oidPartner) {
		this.oidPartner = oidPartner;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getNoProduct() {
		return noProduct;
	}
	public void setNoProduct(String noProduct) {
		this.noProduct = noProduct;
	}
	public String getResultAuth() {
		return resultAuth;
	}
	public void setResultAuth(String resultAuth) {
		this.resultAuth = resultAuth;
	}
	public String getSignField() {
		return signField;
	}
	public void setSignField(String signField) {
		this.signField = signField;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getBeIdcard() {
		return beIdcard;
	}
	public void setBeIdcard(String beIdcard) {
		this.beIdcard = beIdcard;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getIdName() {
		return idName;
	}
	public void setIdName(String idName) {
		this.idName = idName;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getIssuingAuthority() {
		return issuingAuthority;
	}
	public void setIssuingAuthority(String issuingAuthority) {
		this.issuingAuthority = issuingAuthority;
	}
	public String getValidityPeriod() {
		return validityPeriod;
	}
	public void setValidityPeriod(String validityPeriod) {
		this.validityPeriod = validityPeriod;
	}
	public String getInfoOrder() {
		return infoOrder;
	}
	public void setInfoOrder(String infoOrder) {
		this.infoOrder = infoOrder;
	}
	
	
}
