package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.query.AfSignRewardQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfSignRewardDao;
import com.ald.fanbei.api.dal.domain.AfSignRewardDo;
import com.ald.fanbei.api.biz.service.AfSignRewardService;

import java.util.Date;
import java.util.List;


/**
 * 分类运营位配置ServiceImpl
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-05-07 13:51:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afSignRewardService")
public class AfSignRewardServiceImpl  implements AfSignRewardService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfSignRewardServiceImpl.class);
   
    @Resource
    private AfSignRewardDao afSignRewardDao;


    @Override
    public boolean isExist(Long userId){
        return  afSignRewardDao.isExist(userId)> 0 ? true :false;
    }

    @Override
    public int sumSignDays(Long userId,Date startTime){
        return  afSignRewardDao.sumSignDays(userId,startTime);
    }

    @Override
    public List<AfSignRewardDo> getRewardDetailList(AfSignRewardQuery query){
        return  afSignRewardDao.getRewardDetailList(query);
    }

    @Override
    public int saveRecord(AfSignRewardDo afSignRewardDo){
        return  afSignRewardDao.saveRecord(afSignRewardDo);
    }

    @Override
    public boolean checkUserSign(Long userId){
        return  afSignRewardDao.checkUserSign(userId)> 0 ? true :false;
    }
}