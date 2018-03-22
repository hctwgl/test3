package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
/**
 * 颜值测试游戏图片对象
 * @author liutengyuan 
 * @date 2018年3月22日
 */
public class AfFacescoreImgDo extends AbstractSerial {

	private static final long serialVersionUID = 1L;
	private long id;
	private String imgUrl;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
}
