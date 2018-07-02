package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfRecycleDo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 
 *@类描述：有得卖 回收业务  页面访问
 *@author weiqingeng 2018年2月27日  09:47:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class AfRecycleViewQuery{

	private static final long serialVersionUID = -722303985401230132L;
	
	private Integer type; //访问类型 1：回收 2：返现
	private Long userId;//用户id
	private Integer qty;//访问次数

	@Override
	public String toString() {
		return "AfRecycleViewQuery{" +
				"type=" + type +
				", userId=" + userId +
				", qty=" + qty +
				'}';
	}
	public AfRecycleViewQuery(){

	}
	public AfRecycleViewQuery(Long userId,Integer type){
		this.userId = userId;
		this.type = type;
	}
	public AfRecycleViewQuery(Integer type){
		this.type = type;
	}
}
