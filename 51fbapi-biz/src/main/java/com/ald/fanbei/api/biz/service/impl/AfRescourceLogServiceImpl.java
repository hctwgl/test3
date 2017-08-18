/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfRescourceLogService;
import com.ald.fanbei.api.dal.dao.AfResourceLogDao;
import com.ald.fanbei.api.dal.domain.AfResourceLogDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年5月8日下午5:14:32
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRescourceLogService")
public class AfRescourceLogServiceImpl implements AfRescourceLogService {

	@Resource
	AfResourceLogDao afRescourceLogDao;
	@Override
	public int addResourceLog(AfResourceLogDo afResourceLogDo) {
		return afRescourceLogDao.addResourceLog(afResourceLogDo);
	}

	
	@Override
	public int updateResourceLog(AfResourceLogDo afResourceLogDo) {
		return afRescourceLogDao.updateResourceLog(afResourceLogDo);
	}

	
	@Override
	public AfResourceLogDo selectResourceLogTypeAndSecType(String type, String secType, Date gmtCreate) {
		return afRescourceLogDao.selectResourceLogTypeAndSecType(type, secType, gmtCreate);
	}

}
