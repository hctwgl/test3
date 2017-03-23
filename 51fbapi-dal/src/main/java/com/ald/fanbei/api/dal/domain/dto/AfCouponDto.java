/**
 * 
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfCouponDo;

/**
 * @类描述：
 * @author suweili 2017年3月23日上午11:04:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfCouponDto extends AfCouponDo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer userAlready;//该用户领取数(未登录时为0)
	/**
	 * @return the userAlready
	 */
	public Integer getUserAlready() {
		return userAlready;
	}
	/**
	 * @param userAlready the userAlready to set
	 */
	public void setUserAlready(Integer userAlready) {
		this.userAlready = userAlready;
	}


}
