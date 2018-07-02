/**
 * 
 */
package com.ald.fanbei.api.common.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luoxiao @date 2018年3月16日 下午4:46:20
 * @类描述：AfTradeSettleOrderStatus.java
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 *
 */
public enum AfTradeSettleOrderStatus {
	
	EXTRACTABLE("EXTRACTABLE","待提现"),
	EXTRACTED("EXTRACTED","已提现"),
	EXTRACTING("EXTRACTING","提现中"),
	NOT_EXTRACTABLE("NOT_EXTRACTABLE","不可提现"),
	CANCER("CANCER","取消结算");
	
	
	private String code;

	private String description;

	private static Map<String, AfTradeSettleOrderStatus> codeMap = null;

	AfTradeSettleOrderStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public static OrderStatus findEnumByCode(String code) {
		for (OrderStatus goodSource : OrderStatus.values()) {
			if (goodSource.getCode().equals(code)) {
				return goodSource;
			}
		}
		return null;
	}

	public static Map<String, AfTradeSettleOrderStatus> getCodeEnumMap() {
		if (codeMap != null && codeMap.size() > 0) {
			return codeMap;
		}
		codeMap = new HashMap<String, AfTradeSettleOrderStatus>();
		for (AfTradeSettleOrderStatus item : AfTradeSettleOrderStatus.values()) {
			codeMap.put(item.getCode(), item);
		}
		return codeMap;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
