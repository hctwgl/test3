package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAftersaleApplyService;
import com.ald.fanbei.api.dal.dao.AfAftersaleApplyDao;



/**
 * 售后申请ServiceImpl
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-08 16:15:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAftersaleApplyService")
public class AfAftersaleApplyServiceImpl implements AfAftersaleApplyService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAftersaleApplyServiceImpl.class);
   
    @Resource
    private AfAftersaleApplyDao afAftersaleApplyDao;

}