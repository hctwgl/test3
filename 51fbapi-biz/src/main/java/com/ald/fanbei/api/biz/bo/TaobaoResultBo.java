package com.ald.fanbei.api.biz.bo;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 * 
 * @类描述：淘宝商品实体消息
 * @author xiaotianjian 2017年2月26日下午1:51:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class TaobaoResultBo extends AbstractSerial{
	
	private static final long serialVersionUID = 6435740945849835607L;
	
	private Date timestamp;     //查询返回结果
	private String data_id;     //数据id
	private String item_id;     //商品id 
	private TaobaoItemInfoBo item_info;  	//商品相关信息
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @return the data_id
	 */
	public String getData_id() {
		return data_id;
	}
	/**
	 * @param data_id the data_id to set
	 */
	public void setData_id(String data_id) {
		this.data_id = data_id;
	}
	/**
	 * @return the item_id
	 */
	public String getItem_id() {
		return item_id;
	}
	/**
	 * @param item_id the item_id to set
	 */
	public void setItem_id(String item_id) {
		this.item_id = item_id;
	}
	/**
	 * @return the item_info
	 */
	public TaobaoItemInfoBo getItem_info() {
		return item_info;
	}
	/**
	 * @param item_info the item_info to set
	 */
	public void setItem_info(TaobaoItemInfoBo item_info) {
		this.item_info = item_info;
	}
	
	
}
	
	


