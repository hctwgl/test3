package com.ald.fanbei.api.common.enums;


/**
 * 
 * @类描述：自营商城结算单打款状态
 * @author weiqingeng 2017年12月14日下午2:34:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum SupplierSettlementOrderPayStatus {

	//状态  0：待打款  1：打款中  2：已打款成功 3：打款失败
	NEED_PAY(0, "待打款"), PAYING(1, "打款中"), PAY_SUCCESS(2,"已打款成功"), PAY_FAILED(3,"打款失败");

	private int status;

	private String description;

	private SupplierSettlementOrderPayStatus(int status, String description) {
		this.status = status;
		this.description = description;
	}

	/**
	 * 用来判断客户端的status值是否为当前枚举的中状态值，防止不必要的值出现
	 * @param status
	 * @return
	 */
	public Integer valueOfPayStatus(int status){
		for(SupplierSettlementOrderPayStatus payStatus : SupplierSettlementOrderPayStatus.values()){
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
