package com.ald.fanbei.api.biz.bo.aassetside.edspay;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @类现描述：钱包平台退回债权请求实体
 * @author chengkang 2017年11月29日 14:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayBackCreditReqBo implements Serializable {

	private static final long serialVersionUID = 4347678991772430075L;
	/**
	 * 退回订单列表
	 */
	private List<String> orderNos;
	
	/**
	 * 债权类型，0现金贷，1消费分期
	 */
	private Integer debtType;


	private Integer type ;//回传类型 1审核结果，2放款结果
	private Integer code ;//回传结果:0成功1失败
	private String message ;

	private Date loanTime;

	public Date getLoanTime() {
		return loanTime;
	}

	public void setLoanTime(Date loanTime) {
		this.loanTime = loanTime;
	}

	public List<String> getOrderNos() {
		return orderNos;
	}
	public void setOrderNos(List<String> orderNos) {
		this.orderNos = orderNos;
	}
	public Integer getDebtType() {
		return debtType;
	}
	public void setDebtType(Integer debtType) {
		this.debtType = debtType;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "EdspayBackCreditReqBo{" +
				"orderNos=" + orderNos +
				", debtType=" + debtType +
				", type=" + type +
				", code=" + code +
				", message='" + message + '\'' +
				'}';
	}
}
