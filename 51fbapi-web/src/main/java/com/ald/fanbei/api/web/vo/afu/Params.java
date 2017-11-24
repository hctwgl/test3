package com.ald.fanbei.api.web.vo.afu;

import java.io.Serializable;
/**
 *  
 * @类描述：响应数据父节点类
 * @author chenxuankai 2017年11月23日11:35:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class Params implements Serializable {
	
	private String tx;//事物代号
	private String data;//内容节点
	private String version;//Api版本信息
	
	public Params() {}

	public Params(String tx, String data, String version) {
		super();
		this.tx = tx;
		this.data = data;
		this.version = version;
	}

	public String getTx() {
		return tx;
	}

	public void setTx(String tx) {
		this.tx = tx;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	
	
}
