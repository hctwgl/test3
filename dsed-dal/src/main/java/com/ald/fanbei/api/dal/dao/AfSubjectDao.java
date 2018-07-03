package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSubjectDo;
import com.ald.fanbei.api.dal.domain.query.AfSubjectQuery;

/**
 * @类描述：
 * @author 江荣波  2017年6月6日上午9:49:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSubjectDao {

	/**
	 * 通过商品id获取商品信息
	 * @param rid
	 * @return
	 */
	int addSubject(AfSubjectDo afSubjectDo);

	List<AfSubjectDo> listAllSubject(AfSubjectQuery query);

	int deleteSubject(@Param("activityId")long activityId);

	List<AfSubjectDo> listLevelSubject(AfSubjectQuery query);

	List<AfGoodsDo> listQualitySubjectGoods();

	AfSubjectDo getSubjectInfoById(@Param("subjectId")String id);

	AfSubjectDo getParentSubjectInfoById(@Param("subjectId")String subjectId);

	List<AfSubjectDo> listAllParentSubjectByTag(@Param("tag")String tag);

	List<AfSubjectDo> listByParentIdAndLevel(AfSubjectDo queryAfSubject);
	
	AfSubjectDo getSubjectInfoByTag(@Param("tag")String tag);

	
}