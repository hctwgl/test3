package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.DsedCommitRecordService;
import com.ald.fanbei.api.dal.dao.DsedCommitRecordDao;
import com.ald.fanbei.api.dal.domain.DsedCommitRecordDo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @类现描述：
 * @author fumeiai 2017年4月24日 下午18：55：52
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afCommitRecordService")
public class DsedCommitRecordServiceImpl implements DsedCommitRecordService {
	@Resource
	DsedCommitRecordDao commitRecordDao;

	@Override
	public int addRecord(DsedCommitRecordDo dsedCommitRecordDo) {
		return commitRecordDao.addRecord(dsedCommitRecordDo);
	}

	@Override
	public DsedCommitRecordDo getRecordByTypeAndRelateId(String relate_id) {
		return commitRecordDao.getRecordByTypeAndRelateId(relate_id);
	}

}
