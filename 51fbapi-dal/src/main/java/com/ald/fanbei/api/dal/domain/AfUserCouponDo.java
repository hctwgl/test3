package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @类描述：用户优惠券
 * @author hexin 2017年1月20日下午11:08:05
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
public class AfUserCouponDo extends AbstractSerial{

	private static final long serialVersionUID = 105301942297258703L;
	private	Long	rid;
	private Date 	gmtCreate;
	private Date 	gmtModified;
	private Long	userId;
	private Long	couponId;//优惠券id
	private Date	gmtStart;//开始时间
	private Date	gmtEnd;//截止时间
	private String  status;//状态 【 EXPIRE:过期 ; NOUSE:未使用 ， USED:已使】
	private Date	gmtUse;//使用时间
	private String	sourceType;//'获取来源【REGIST：注册， INVITE：邀请, SPECIAL：专场, GAME：游戏,REDRAIN：红包雨,USERWAKE：用户唤醒, RECYCLE:有得卖回收业务】',
	private String	type;//获取来源【REGIST：注册， INVITE：邀请 】
	private String	sourceRef = "SYS"; //对应数据库默认值

	public AfUserCouponDo(){

	}

	public AfUserCouponDo(Long uid, Long couponId, String status,String	sourceType,Date gmtStart,Date gmtEnd){
		this.userId = uid;
		this.couponId = couponId;
		this.status = status;
		this.sourceType = sourceType;
		this.gmtStart = gmtStart;
		this.gmtEnd = gmtEnd;
	}

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

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCouponId() {
		return couponId;
	}

	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}

	public Date getGmtStart() {
		return gmtStart;
	}

	public void setGmtStart(Date gmtStart) {
		this.gmtStart = gmtStart;
	}

	public Date getGmtEnd() {
		return gmtEnd;
	}

	public void setGmtEnd(Date gmtEnd) {
		this.gmtEnd = gmtEnd;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getGmtUse() {
		return gmtUse;
	}

	public void setGmtUse(Date gmtUse) {
		this.gmtUse = gmtUse;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSourceRef() {
		return sourceRef;
	}

	public void setSourceRef(String sourceRef) {
		this.sourceRef = sourceRef;
	}
}
