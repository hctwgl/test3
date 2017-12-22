package com.ald.fanbei.api.web.vo.afu;

import java.io.Serializable;
/** 
 * @类描述：请求数据子节点类
 * @author chenxuankai 2017年11月23日14:27:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ParamsSon implements Serializable {
	
	private String tx;//请求的业务类型编号
	private RequestData data;//查询的内容
	
	public ParamsSon(String tx, RequestData data) {
		super();
		this.tx = tx;
		this.data = data;
	}
	public ParamsSon() {}
	public String getTx() {
		return tx;
	}
	public void setTx(String tx) {
		this.tx = tx;
	}
	public RequestData getData() {
		return data;
	}
	public void setData(RequestData data) {
		this.data = data;
	}	
	
}
