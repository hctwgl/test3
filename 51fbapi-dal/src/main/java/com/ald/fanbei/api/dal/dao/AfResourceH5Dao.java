package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfResourceH5Do;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;
import com.ald.fanbei.api.dal.domain.query.AfResourceH5Query;

/**
 * h5资源管理Dao
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:42:22 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceH5Dao extends BaseDao<AfResourceH5Do, Long> {
	List<AfResourceH5Do> listResourceH5(AfResourceH5Query query);

	AfResourceH5Do getResourceH5ById(@Param("resourceH5Id") Long resourceH5Id);

	int deleteResourceH5(AfResourceH5Do resourceH5);

	int addResourceH5(AfResourceH5Do resourceH5);

	int updateResourceH5(AfResourceH5Do resourceH5);

	int editResourceH5(AfResourceH5Do resourceH5Do);
	String editResourceH5Status(AfResourceH5Do resourceH5Do);

	int deleteById(AfResourceH5Do resourceH5Do);

	List<AfResourceH5Do> getListByCommonCondition(AfResourceH5Do queryDo);
	List<AfResourceH5Dto> selectByStatus();
	
}
