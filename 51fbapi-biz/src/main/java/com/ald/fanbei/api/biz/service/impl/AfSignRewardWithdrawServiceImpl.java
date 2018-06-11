package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.dto.AfSignRewardWithdrawDto;
import com.ald.fanbei.api.dal.domain.query.AfSignRewardWithdrawQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfSignRewardWithdrawDao;
import com.ald.fanbei.api.dal.domain.AfSignRewardWithdrawDo;
import com.ald.fanbei.api.biz.service.AfSignRewardWithdrawService;

import java.math.BigDecimal;
import java.util.List;


/**
 * 分类运营位配置ServiceImpl
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 14:01:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afSignRewardWithdrawService")
public class AfSignRewardWithdrawServiceImpl  implements AfSignRewardWithdrawService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfSignRewardWithdrawServiceImpl.class);
   
    @Resource
    private AfSignRewardWithdrawDao afSignRewardWithdrawDao;

    @Override
    public List<AfSignRewardWithdrawDto> getWithdrawList(AfSignRewardWithdrawQuery query){
        return afSignRewardWithdrawDao.getWithdrawList(query);
    }

    @Override
    public int saveRecord(AfSignRewardWithdrawDo afSignRewardWithdrawDo){
        return afSignRewardWithdrawDao.saveRecord(afSignRewardWithdrawDo);
    }

    @Override
    public BigDecimal getTodayWithdrawAmount() {
        return afSignRewardWithdrawDao.getTodayWithdrawAmount();
    }
}