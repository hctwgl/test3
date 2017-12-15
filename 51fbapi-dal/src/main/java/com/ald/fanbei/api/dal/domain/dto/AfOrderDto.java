/**
 * 
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;

/**
 * @类描述：
 * @author suweili 2017年3月23日上午11:04:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfOrderDto extends AfOrderDo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer num;

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
}
