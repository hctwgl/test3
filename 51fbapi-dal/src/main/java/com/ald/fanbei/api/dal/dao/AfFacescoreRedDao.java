package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfFacescoreRedDo;

/**
 * 颜值测试红包表实体类Dao
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-12 16:37:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfFacescoreRedDao extends BaseDao<AfFacescoreRedDo, Long> {

	AfFacescoreRedDo getImageUrlByUserId(@Param("userId")Long userId);

	int addRed(AfFacescoreRedDo redDo);

	List<com.ald.fanbei.api.dal.domain.AfFacescoreImgDo> getAllRedImg();



    

}
