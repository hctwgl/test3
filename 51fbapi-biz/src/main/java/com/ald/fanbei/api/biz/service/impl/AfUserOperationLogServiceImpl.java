package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserOperationLogService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.dal.dao.AfUserOperationLogDao;
import com.ald.fanbei.api.dal.domain.AfUserOperationLogDo;

/**
 * @类现描述：用户特殊操作日志类型Servicep实现
 * @author chengkang 2017年6月4日 下午4:27:00
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("afUserOperationLogService")
public class AfUserOperationLogServiceImpl extends BaseService implements AfUserOperationLogService {
	
	@Resource
	AfUserOperationLogDao afUserOperationLogDao;
	
	
	@Override
	public int addUserOperationLog(AfUserOperationLogDo afUserOperationLogDo){
		int result = 0;
		try {
			result = afUserOperationLogDao.addUserOperationLog(afUserOperationLogDo);
		} catch (Exception e) {
			logger.error("insert af_user_operation_log error,msg=" + e);
		}
		return result;
	}
	
	@Override
	public Integer getNumsByUserAndType(AfUserOperationLogDo afUserOperationLogDo){
		return afUserOperationLogDao.getNumsByUserAndType(afUserOperationLogDo);
	}
}
