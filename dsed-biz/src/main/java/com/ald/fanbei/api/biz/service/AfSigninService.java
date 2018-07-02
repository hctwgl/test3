/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfSigninDo;

/**
 * @类描述：
 * @author suweili 2017年2月7日下午4:48:27
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSigninService {
	
	/**
	 * 添加签名
	 * @param afSigninDo
	 * @return
	 */
	int addSignin(AfSigninDo afSigninDo);
	
	/**
	 * 修改签名记录
	 * @param afSigninDo
	 * @return
	 */
	int changeSignin(AfSigninDo afSigninDo);
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	AfSigninDo selectSigninByUserId(Long userId);

}
