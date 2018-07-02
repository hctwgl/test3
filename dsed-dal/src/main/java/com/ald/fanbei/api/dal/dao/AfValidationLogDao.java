package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfValidationLogDo;

/**
 * @类描述：
 * @author chefeipeng  2017年6月20日上午9:49:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfValidationLogDao {

	/**
	 * 增加记录
	 * @param afValidationLogDo
	 * @return
	 */
	int addValidationLog(AfValidationLogDo afValidationLogDo);
	/**
	 * 更新记录
	 * @param afValidationLogDo
	 * @return
	 */
	int updateValidationLog(AfValidationLogDo afValidationLogDo);

	List<AfValidationLogDo> selectByUserId(AfValidationLogDo afValidationLogDo);

	int countFailNumWithin24H(AfValidationLogDo afValidationLogDo);
	
}
