package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfSignRewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfSignRewardExtDao;
import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
import com.ald.fanbei.api.biz.service.AfSignRewardExtService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
    @Resource
    AfSignRewardService afSignRewardService;

    @Override
    public AfSignRewardExtDo selectByUserId(Long userId){
        return afSignRewardExtDao.selectByUserId(userId);
    }

    @Override
    public int extractMoney(Long userId, BigDecimal amount){
        return afSignRewardExtDao.extractMoney(userId,amount);
    }

    @Override
    public int updateSignRewardExt(AfSignRewardExtDo afSignRewardExtDo){
        return afSignRewardExtDao.updateSignRewardExt(afSignRewardExtDo);
    }

    @Override
    public int saveRecord(AfSignRewardExtDo afSignRewardExtDo){
        return afSignRewardExtDao.saveRecord(afSignRewardExtDo);
    }

    @Override
    public int increaseMoney(AfSignRewardExtDo afSignRewardExtDo){
        return afSignRewardExtDao.increaseMoney(afSignRewardExtDo);
    }

    @Override
    public List<AfSignRewardExtDo> selectByUserIds(List<Long> userIds){
        return afSignRewardExtDao.selectByUserIds(userIds);
    }

    @Override
    public int saveRecordBatch(List<AfSignRewardExtDo> list ){
        return afSignRewardExtDao.saveRecordBatch(list);
    }

    @Override
    public int increaseMoneyBtach(List<AfSignRewardExtDo> list ){
        return afSignRewardExtDao.increaseMoneyBtach(list);
    }

    @Override
    public Map<String,Object> getHomeInfo(Long userId){
        Map<String,Object> map = new HashMap<>();
        AfSignRewardExtDo afSignRewardExtDo = selectByUserId(userId);
        if(null == afSignRewardExtDo){
            //新增SignRewardExt
            afSignRewardExtDo.setIsOpenRemind(0);
            afSignRewardExtDo.setUserId(userId);
            afSignRewardExtDo.setGmtModified(new Date());
            afSignRewardExtDo.setGmtCreate(new Date());
            afSignRewardExtDo.setAmount(BigDecimal.ZERO);
            afSignRewardExtDo.setCycleDays(10);
            afSignRewardExtDo.setFirstDayParticipation(null);
            afSignRewardExtDo.setIsDelete(0);
            saveRecord(afSignRewardExtDo);
            //签到提醒
            map.put("isOpenRemind","N");
            //是否有余额
            map.put("rewardAmount",BigDecimal.ZERO);
            //是否有补签
            map.put("supplementSignDays",0);
        }else if(null != afSignRewardExtDo){
            //签到提醒
            map.put("isOpenRemind",afSignRewardExtDo.getIsOpenRemind()>0?"Y":"N");
            //是否有余额
            map.put("rewardAmount",afSignRewardExtDo.getAmount());
            //是否有补签
            int count = afSignRewardService.supplementSign(afSignRewardExtDo,0);
            map.put("supplementSignDays",count);
        }
        return map;
    }

}