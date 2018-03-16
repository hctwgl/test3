package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfOrderDo;

import java.math.BigDecimal;

/**
 * 
 *@类描述：有得卖 回收业务 系统系数
 *@author weiqingeng 2018年2月27日  09:47:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRecycleRatioQuery extends Page<AfOrderDo>{

	private static final long serialVersionUID = -722303985401230132L;

	private Long id;

	private BigDecimal ratio;//比例

	private Integer type;//类型

	public AfRecycleRatioQuery(){

	}

	public AfRecycleRatioQuery(Integer type){
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getRatio() {
		return ratio;
	}

	public void setRatio(BigDecimal ratio) {
		this.ratio = ratio;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
