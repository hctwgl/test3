package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.dal.dao.AfSmsRecordDao;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;

/**
 * 
 * @类描述：
 * @author Xiaotianjian 2017年1月19日下午3:37:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("afSmsRecordService")
public class AfSmsRecordServiceImpl implements AfSmsRecordService{

	@Resource
	AfSmsRecordDao afSmsRecordDao;
	
	@Override
	public int addSmsRecord(AfSmsRecordDo afSmsRecordDo) {
		return afSmsRecordDao.addSmsRecord(afSmsRecordDo);
	}

	@Override
	public AfSmsRecordDo getLatestByUidType(String mobile, String type) {
		return afSmsRecordDao.getLatestByUidType(mobile, type);
	}
	
	@Override
	public int updateSmsIsCheck(Integer id) {
		return afSmsRecordDao.updateSmsIsCheck(id);
	}


	@Override
	public int updateSmsFailCount(AfSmsRecordDo afSmsRecordDo) {
		return afSmsRecordDao.updateSmsFailCount(afSmsRecordDo);
	}

	@Override
	public AfSmsRecordDo getLatestByMobileCode(String mobile, String verifyCode) {
		return afSmsRecordDao.getLatestByMobileCode(mobile, verifyCode);
	}

}
