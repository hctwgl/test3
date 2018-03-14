package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfSubjectDo;
import com.ald.fanbei.api.dal.domain.query.AfSubjectQuery;
/**
 * 
 * @类描述：
 * @author 江荣波 2017年3月2日上午11:27:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSubjectService {
	

	int addSubject(AfSubjectDo afSubjectDo);

	List<AfSubjectDo> listAllSubject(AfSubjectQuery query);

	int deleteSubject(long parseLong);
	
	List<AfSubjectDo> listLevelSubject(AfSubjectQuery query);
	
	AfSubjectDo getSubjectInfoById(String id);

	AfSubjectDo getParentSubjectInfoById(String subjectId);


	List<AfSubjectDo>  listAllParentSubjectByTag(String tag);

	List<AfSubjectDo> listByParentIdAndLevel(AfSubjectDo queryAfSubject);
	
	AfSubjectDo getSubjectInfoByTag(String tag);



}
