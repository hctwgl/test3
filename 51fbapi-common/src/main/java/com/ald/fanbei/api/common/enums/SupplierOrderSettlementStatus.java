package com.ald.fanbei.api.common.enums;


/**
 * 
 * @类描述：自营商城结算订单  结算状态
 * @author weiqingeng 2017年12月14日下午2:34:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum SupplierOrderSettlementStatus {

	//结算订单 结算状态，0：待结算 ,1：已结算， 2 结算中
	NEED_SETTLEMENT(0, "待结算"), SETTLEMENT_ING(1, "结算中"),SETTLEMENT_SUCCESS(2, "已结算"), SETTLEMENT_FAILED(2,"结算失败");

	private int status;

	private String description;

	private SupplierOrderSettlementStatus(int status, String description) {
		this.status = status;
		this.description = description;
	}

	/**
	 * 用来判断客户端的status值是否为当前枚举的中状态值，防止不必要的值出现
	 * @param status
	 * @return
	 */
	public Integer valueOfPayStatus(int status){
		for(SupplierOrderSettlementStatus payStatus : SupplierOrderSettlementStatus.values()){
			if(payStatus.getStatus() == status){
				return payStatus.getStatus();
			}
		}
		return null;
	}
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
