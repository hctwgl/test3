/*
 *@Copyright (c) 2016, 杭州喜马拉雅家居有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午4:05:01
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSmsRecordService {
	/**
	 * 增加短信发送短信记录
	 *@param hoaSmsRecordDo
	 *@return
	 */
	int addSmsRecord(AfSmsRecordDo hoaSmsRecordDo);
	
	/**
	 * 根据手机号和类型获取最近一次发送的短信
	 * 
	 *@param mobile
	 *@param type
	 *@return
	 */
	AfSmsRecordDo getLatestByUidType(String mobile,Integer type);
	
	/**
	 * 更新验证码已经验证
	 *@param id
	 *@return
	 */
	int updateSmsIsCheck(Integer id);
}
