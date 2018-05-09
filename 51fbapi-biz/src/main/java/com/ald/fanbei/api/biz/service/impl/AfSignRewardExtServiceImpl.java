package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfSignRewardExtDao;
import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
import com.ald.fanbei.api.biz.service.AfSignRewardExtService;

import java.math.BigDecimal;


/**
 * 分类运营位配置ServiceImpl
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 14:01:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afSignRewardExtService")
public class AfSignRewardExtServiceImpl  implements AfSignRewardExtService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfSignRewardExtServiceImpl.class);
   
    @Resource
    private AfSignRewardExtDao afSignRewardExtDao;

    @Override
    public AfSignRewardExtDo selectByUserId(Long userId){
        return afSignRewardExtDao.selectByUserId(userId);
    }

    @Override
    public int extractMoney(Long userId, BigDecimal amount){
        return afSignRewardExtDao.extractMoney(userId,amount);
    }


}