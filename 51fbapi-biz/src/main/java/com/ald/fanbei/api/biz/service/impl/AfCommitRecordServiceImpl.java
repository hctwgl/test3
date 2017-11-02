package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfCommitRecordService;
import com.ald.fanbei.api.dal.dao.AfCommitRecordDao;
import com.ald.fanbei.api.dal.domain.AfCommitRecordDo;

/**
 * @类现描述：
 * @author fumeiai 2017年4月24日 下午18：55：52
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afCommitRecordService")
public class AfCommitRecordServiceImpl implements AfCommitRecordService {
	@Resource
	AfCommitRecordDao commitRecordDao;

	@Override
	public int addRecord(AfCommitRecordDo afCommitRecordDo) {
		return commitRecordDao.addRecord(afCommitRecordDo);
	}

	@Override
	public AfCommitRecordDo getRecordByTypeAndRelateId(String relate_id) {
		return commitRecordDao.getRecordByTypeAndRelateId(relate_id);
	}

}
