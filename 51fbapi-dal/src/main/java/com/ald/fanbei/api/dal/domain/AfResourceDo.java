package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

/**
 *@类描述：
 *@author Xiaotianjian 2017年1月20日上午10:09:09
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
public class AfResourceDo extends AbstractSerial {
	
	private static final long serialVersionUID = -5566198924650170281L;
	
	private Long rid;//用户id
	private Date gmtCreate;
	private Date gmtModified;
	private String creator;
	private String modifier;
	private String dataType;//配置类型，S:系统配置,B:业务配置
	private String type;//配置类型，即配置的KEY，用于定位配置；所有字母大写，多个字母中间用下划线“_”分割；如：用户白名单类型USER_WHITE_LIST
	private String typeDesc;//配置类型描述，如针对TYPE=USER_WHITE_LIST该值可描述为：用户白名单列表
	private String name;//名称
	private String value;//值
	private String description;//描述
	private String secType;//类型，可针对某一类型的配置做分类
	private String value1;//扩展值1
	private String value2;//扩展值2
	private String value3;//扩展值3
	private String value4;//扩展值4
	private String value5;//扩展值5
	private Long sort;//排序
	private String pic1;
	private String pic2;

	@Override
	public String toString() {
		return "AfResourceDo [rid=" + rid + ", gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + ", creator="
				+ creator + ", modifier=" + modifier + ", dataType=" + dataType + ", type=" + type + ", typeDesc="
				+ typeDesc + ", name=" + name + ", value=" + value + ", description=" + description + ", secType="
				+ secType + ", value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4
				+ ", sort=" + sort + ", pic1=" + pic1 + ", pic2=" + pic2 + "]";
	}
	


}
