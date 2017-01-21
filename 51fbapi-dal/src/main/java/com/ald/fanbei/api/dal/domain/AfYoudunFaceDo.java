package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;
/**
 * 
 *@类描述：有盾人脸识别对象类
 *@author 陈金虎 2017年1月21日 下午3:41:02
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfYoudunFaceDo extends AbstractSerial{

	private static final long serialVersionUID = -7911488819341968763L;
	private String backCard;
	private String birthday;
	private String oidPartner;
	private String photoLiving;
	private String resultAuth;
	private String failReason;
	private Long rid;
	private String signField;
	private Integer age;
	private String gender;
	private String userId;
	private String validityPeriod;
	private String photoGet;
	private String noOrder;
	private String idName;
	private String frontCard;
	private String infoOrder;
	private String nation;
	private String sign;
	private Date gmtCreate;
	private String photoGrid;
	private String address;
	private String issuingAuthority;
	private BigDecimal beIdcard;
	private String noProduct;
	private String idNo;
	public String getBackCard() {
		return backCard;
	}
	public void setBackCard(String backCard) {
		this.backCard = backCard;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getOidPartner() {
		return oidPartner;
	}
	public void setOidPartner(String oidPartner) {
		this.oidPartner = oidPartner;
	}
	public String getPhotoLiving() {
		return photoLiving;
	}
	public void setPhotoLiving(String photoLiving) {
		this.photoLiving = photoLiving;
	}
	public String getResultAuth() {
		return resultAuth;
	}
	public void setResultAuth(String resultAuth) {
		this.resultAuth = resultAuth;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public String getSignField() {
		return signField;
	}
	public void setSignField(String signField) {
		this.signField = signField;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getValidityPeriod() {
		return validityPeriod;
	}
	public void setValidityPeriod(String validityPeriod) {
		this.validityPeriod = validityPeriod;
	}
	public String getPhotoGet() {
		return photoGet;
	}
	public void setPhotoGet(String photoGet) {
		this.photoGet = photoGet;
	}
	public String getNoOrder() {
		return noOrder;
	}
	public void setNoOrder(String noOrder) {
		this.noOrder = noOrder;
	}
	public String getIdName() {
		return idName;
	}
	public void setIdName(String idName) {
		this.idName = idName;
	}
	public String getFrontCard() {
		return frontCard;
	}
	public void setFrontCard(String frontCard) {
		this.frontCard = frontCard;
	}
	public String getInfoOrder() {
		return infoOrder;
	}
	public void setInfoOrder(String infoOrder) {
		this.infoOrder = infoOrder;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getPhotoGrid() {
		return photoGrid;
	}
	public void setPhotoGrid(String photoGrid) {
		this.photoGrid = photoGrid;
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
	public BigDecimal getBeIdcard() {
		return beIdcard;
	}
	public void setBeIdcard(BigDecimal beIdcard) {
		this.beIdcard = beIdcard;
	}
	public String getNoProduct() {
		return noProduct;
	}
	public void setNoProduct(String noProduct) {
		this.noProduct = noProduct;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	
}
