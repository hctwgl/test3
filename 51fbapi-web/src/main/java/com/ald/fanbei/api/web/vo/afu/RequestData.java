package com.ald.fanbei.api.web.vo.afu;

import java.io.Serializable;
/** 
 * @类描述：请求数据data节点类
 * @author chenxuankai 2017年11月23日14:46:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RequestData implements Serializable {

	private String name;//姓名
	private String idNo;//身份证号
	
	public RequestData(String name, String idNo) {
		super();
		this.name = name;
		this.idNo = idNo;
	}
	public RequestData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}	
	
}
