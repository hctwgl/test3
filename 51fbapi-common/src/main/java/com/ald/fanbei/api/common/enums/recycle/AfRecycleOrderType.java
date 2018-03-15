package com.ald.fanbei.api.common.enums.recycle;


/**
 * @类描述：有得卖 回收业务订单状态  订单状态 1：待确认 2：待上门 3：待检测 6：待发货 7：待收货 8：待支付  20:确认发券 66：已完成 98：终止退回 99：已终止
 * @author weiqingeng
 * @date 2018年3月8日下午5:57:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfRecycleOrderType {
	PENDING_CONFIRM(1, "待确认"),
	PENDING_COMING(2, "待上门"),
	PENDING_CHECK(3, "待检测"),
	PENDING_DELIVER(6, "待发货"),
	PENDING_RECEIVE(7, "待收货"),
	PENDING_PAY(8, "待支付"),
	CONFIRM_PAY(20, "确认发券"),
	FINISH(66, "已完成"),
	FINISH_REBACK(99, "终止退回"),
	TERMINATED(99, "已终止"),


	;

	private Integer code;

	private String description;

	AfRecycleOrderType(Integer code, String description) {
		this.code = code;
		this.description = description;
	}

	public static AfRecycleOrderType findEnumByCode(Integer code) {
		for (AfRecycleOrderType goodSource : AfRecycleOrderType.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
			}
		}
		return null;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
