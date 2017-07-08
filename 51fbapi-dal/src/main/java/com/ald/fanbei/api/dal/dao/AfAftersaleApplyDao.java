package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;


/**
 * 售后申请Dao
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-08 16:15:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAftersaleApplyDao {

	/**
    * 增加记录
    * @param afAftersaleApplyDo
    * @return
    */
    int saveRecord(AfAftersaleApplyDo afAftersaleApplyDo);

    /**
     * 更新记录
     * @param afAftersaleApplyDo
     * @return
     */
     int updateById(AfAftersaleApplyDo afAftersaleApplyDo);
     /**
      * 查找记录
      * @param afAftersaleApplyDo
      * @return
      */
     AfAftersaleApplyDo getById(@Param("id")Long id);
     
}
