package com.ald.fanbei.api.common.enums;

/**
 * 与西瓜约定的协议类型
 */
public enum  XgxyProtocolType {
	AUTH("数据授权类合同"),
    BINDCARD("绑卡类合同"),
    BORROW("借款类合同"),
    TYING("搭售商品合同"),
    DELAY("展期合同");

    public String desz;

    XgxyProtocolType(String desz) {
        this.desz = desz;
    }

	/**
	 * @return the desz
	 */
	public String getDesz() {
		return desz;
	}

	/**
	 * @param desz the desz to set
	 */
	public void setDesz(String desz) {
		this.desz = desz;
	}

}
