package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserOperationLogDo;


/**
 * @类现描述：用户特殊操作日志类型Service
 * @author chengkang 2017年6月4日 下午4:27:00
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserOperationLogService {

	/**
    * 增加记录
    * @param afUserOperationLogDo
    * @return
    */
    int addUserOperationLog(AfUserOperationLogDo afUserOperationLogDo);
	
    /**
     * 获取对应过滤条件的操作次数（指定用户、指定类型等）
     * @param afUserOperationLogDo
     * @return
     */
	Integer getNumsByUserAndType(AfUserOperationLogDo afUserOperationLogDo);
}
