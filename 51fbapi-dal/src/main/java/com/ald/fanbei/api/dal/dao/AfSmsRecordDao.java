package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午7:45:37
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSmsRecordDao {
	
	/**
	 * 增加短信发送短信记录
	 * 
	 *@param afSmsRecordDo
	 *@return
	 */
	int addSmsRecord(AfSmsRecordDo afSmsRecordDo);
	
	/**
	 * 根据条件查询发送短信记录列表
	 * 
	 *@param afSmsRecordDo
	 *@return
	 */
	List<AfSmsRecordDo> getBySmsRecord(AfSmsRecordDo afSmsRecordDo);
	
	/**
	 * 根据手机号和类型获取最近一次发送的短信
	 * 
	 *@param mobile
	 *@param type
	 *@return
	 */
	AfSmsRecordDo getLatestByUidType(@Param("mobile")String mobile,@Param("type")String type);
	
	/**
	 * 更新验证码已经验证
	 *@param id
	 *@return
	 */
	int updateSmsIsCheck(Integer id);
	
	int updateSmsFailCount(AfSmsRecordDo afSmsRecordDo);
}
