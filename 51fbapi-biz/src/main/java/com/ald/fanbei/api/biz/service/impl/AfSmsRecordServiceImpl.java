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
	AfSmsRecordDao hoaSmsRecordDao;
	
	@Override
	public int addSmsRecord(AfSmsRecordDo AfSmsRecordDo) {
		return hoaSmsRecordDao.addSmsRecord(AfSmsRecordDo);
	}

	@Override
	public AfSmsRecordDo getLatestByUidType(String mobile, String type) {
		return hoaSmsRecordDao.getLatestByUidType(mobile, type);
	}
	
	@Override
	public int updateSmsIsCheck(Integer id) {
		return hoaSmsRecordDao.updateSmsIsCheck(id);
	}

}
