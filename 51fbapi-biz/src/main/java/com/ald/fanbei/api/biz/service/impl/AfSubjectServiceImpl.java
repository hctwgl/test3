package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSubjectService;
import com.ald.fanbei.api.dal.dao.AfSubjectDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSubjectDo;
import com.ald.fanbei.api.dal.domain.query.AfSubjectQuery;

/**
 * 
 *@类描述：AfSubjectServiceImpl
 *@author 何鑫 2017年1月18日  12:51:33
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afSubjectService")
public class AfSubjectServiceImpl implements AfSubjectService{

	@Resource
	private AfSubjectDao afSubjectDao;

	@Override
	public int addSubject(AfSubjectDo afSubjectDo) {
		return afSubjectDao.addSubject(afSubjectDo);
	}

	@Override
	public List<AfSubjectDo> listAllSubject(AfSubjectQuery query) {
		
		return afSubjectDao.listAllSubject(query);
	}

	@Override
	public int deleteSubject(long activityId) {
		return afSubjectDao.deleteSubject(activityId);
	}

	@Override
	public List<AfSubjectDo> listLevelSubject(AfSubjectQuery query) {
		return afSubjectDao.listLevelSubject(query);
	}

	@Override
	public AfSubjectDo getSubjectInfoById(String id) {
		return afSubjectDao.getSubjectInfoById(id);
	}

	@Override
	public AfSubjectDo getParentSubjectInfoById(String subjectId) {
		return afSubjectDao.getParentSubjectInfoById(subjectId);
	}

}
