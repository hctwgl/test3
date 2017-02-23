package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.dto.AfLimitDetailDto;
import com.ald.fanbei.api.dal.domain.query.AfLimitDetailQuery;

/**
 * @类现描述：用户账户日志
 * @author chenjinhu 2017年2月18日 上午11:14:00
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountLogDao {
	
	/**
	 * 增加记录
	 * 
	 * @param afUserAccountLogDo
	 * @return
	 */
	int addUserAccountLog(AfUserAccountLogDo afUserAccountLogDo);
	
	/**
	 * 获取明细列表
	 * @param query
	 * @return
	 */
	List<AfLimitDetailDto> getLimitDetailList(AfLimitDetailQuery query);
}
