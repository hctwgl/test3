package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月29日上午12:44:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BrandCouponRequestBo extends HashMap<String, String>{
	
	private static final long serialVersionUID = 8833336567301068866L;
	
	private String userId;   //用户id
	private String type;   //优惠券类型
	private String pageIndex;   //第几页
	private String pageSize; //唯一标识
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
		this.put("userId", userId);
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
		this.put("type", type);
	}
	/**
	 * @return the pageIndex
	 */
	public String getPageIndex() {
		return pageIndex;
	}
	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
		this.put("pageIndex", pageIndex);
	}
	/**
	 * @return the pageSize
	 */
	public String getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
		this.put("pageSize", pageSize);
	}
}
