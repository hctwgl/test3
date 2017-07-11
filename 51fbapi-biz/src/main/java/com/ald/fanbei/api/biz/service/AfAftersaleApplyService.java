package com.ald.fanbei.api.biz.service;

import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;


/**
 * 售后申请Service
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-08 16:15:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAftersaleApplyService {
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
      * @param id
      * @return
      */
     AfAftersaleApplyDo getById(Long id);
     /**
      * 查找记录
      * @param orderId
      * @return
      */
     AfAftersaleApplyDo getByOrderId(Long orderId);
     /**
      * 查找对应申请编号
      * @param current
      * @return
      */
     String getCurrentLastApplyNo(Date current);
     
}
