package com.ald.fanbei.api.common.enums;



/**  
 * @Title: SpringFestivalActivityEnum.java
 * @Package com.ald.fanbei.api.common.enums
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2018年1月3日 下午12:44:41
 * @version V1.0  
 */
public enum SpringFestivalActivityEnum {

	MAIN(0L,"_MAIN_","预热主会场"),
	MAKE_UP(1L,"_MAKE_UP_","美妆"),
	COMPUTER(2L,"_COMPUTER_","电脑"),
	WATCH(3L,"_WATCH_","名表"),
	CLOTHING(4L,"_CLOTHING_","鞋服"),
	HOME_ELECTICITY(5L,"_HOME_ELECTICITY_","家电"),
	BAGS(8L,"_BAGS_","箱包"),
	PHONE(9L,"_PHONE_","手机"),
	ELECTRIC_PRODUCT(6L,"_ELECTRIC_PRODUCT_","数码"),
	SECOND_HAND(7L,"_SECOND_HAND_","食品"),
	TIGER_MACHINE(10L,"_TIGER_MACHINE_","老虎机");
	
	private Long activityId;
	private String tag;
	private String description;
	
	public static String findTagByActivityId(Long activityId){
		String result = "";
		for(SpringFestivalActivityEnum sFestivalActivityEnum : SpringFestivalActivityEnum.values()){
			if (sFestivalActivityEnum.getActivityId().equals(activityId)) {
				result = sFestivalActivityEnum.getTag();
			}
		}
		return result;
	}
	
	private SpringFestivalActivityEnum(Long activityId, String tag, String description) {
		this.activityId = activityId;
		this.tag = tag;
		this.description = description;
	}
	public Long getActivityId() {
		return activityId;
	}
	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
