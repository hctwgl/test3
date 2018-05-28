package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.enums.SignRewardType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
import com.ald.fanbei.api.dal.domain.query.AfSignRewardQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfSignRewardDao;
import com.ald.fanbei.api.dal.domain.AfSignRewardDo;
import com.ald.fanbei.api.biz.service.AfSignRewardService;

import java.math.BigDecimal;
import java.util.*;


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
    @Resource
    AfSignRewardService afSignRewardService;


    @Override
    public boolean isExist(Long userId){
        return  afSignRewardDao.isExist(userId)> 0 ? true :false;
    }

    @Override
    public List<AfSignRewardDo> sumSignDays(Long userId,Date startTime){
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
        return afSignRewardDao.checkUserSign(userId)> 0 ? true :false;
    }

    @Override
    public boolean friendUserSign(Long friendUserId){
        return afSignRewardDao.friendUserSign(friendUserId)> 0 ? true :false;
    }

    @Override
    public int frienddUserSignCount(Long userId,Long friendUserId){
        return afSignRewardDao.frienddUserSignCount(userId,friendUserId);
    }

    @Override
    public boolean frienddUserSignCountToDay(Long userId,Long friendUserId){
        return afSignRewardDao.frienddUserSignCountToDay(userId,friendUserId)> 0 ? true :false;
    }

    @Override
    public int saveRecordBatch(List<AfSignRewardDo> list){
        return afSignRewardDao.saveRecordBatch(list);
    }

    @Override
    public Map<String,String> supplementSign(AfSignRewardExtDo afSignRewardExtDo, int num,String status){
        StringBuffer sb = new StringBuffer();
        Long days ;
        boolean flag = true;
        Map<String,String> map = new HashMap<String,String>();
        Date date = afSignRewardExtDo.getFirstDayParticipation();
        if(date ==null){
            map.put("supplementSignDays","");
            map.put("signDays","");
            return  map;
        }
        int cycle = afSignRewardExtDo.getCycleDays();
        Date startTime;
        Date endTime;
        int maxNum = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.formatDateToYYYYMMdd(date));
        while(flag){
            num ++;
            calendar.add(Calendar.DAY_OF_MONTH,(new BigDecimal(num-1).multiply(new BigDecimal(cycle))).intValue());
            startTime = calendar.getTime();
            calendar.add(Calendar.DAY_OF_MONTH,cycle-1);
            endTime = calendar.getTime();
            if((startTime.getTime() <= DateUtil.formatDateToYYYYMMdd(new Date()).getTime()) &&
                    (endTime.getTime() >= DateUtil.formatDateToYYYYMMdd(new Date()).getTime())){
                flag = false;
                maxNum = Integer.parseInt(DateUtil.getNumberOfDatesBetween(startTime,DateUtil.formatDateToYYYYMMdd(new Date()))+"");
                List<AfSignRewardDo> afSignRewardDos = sumSignDays(afSignRewardExtDo.getUserId(),startTime);
                for(AfSignRewardDo afSignRewardDo : afSignRewardDos){
                    if(StringUtil.equals(SignRewardType.ZERO.getCode().toString(),afSignRewardDo.getType().toString())){
                        days = DateUtil.getNumberOfDatesBetween(startTime,DateUtil.formatDateToYYYYMMdd(afSignRewardDo.getGmtCreate()));
                        sb.append(days+1).append(",");
                    }else if(StringUtil.equals(SignRewardType.ONE.getCode().toString(),afSignRewardDo.getType().toString())){
                        days = DateUtil.getNumberOfDatesBetween(startTime,DateUtil.formatDateToYYYYMMdd(afSignRewardDo.getTime()));
                        sb.append(days+1).append(",");
                    }
                }
                String str = sb.length()>0?sb.deleteCharAt(sb.length()-1).toString():sb.toString();
                map.put("signDays",str);
                map.put("supplementSignDays",signDays(str,maxNum,status));
            }else{
                map= supplementSign(afSignRewardExtDo,num,status);
                return map;
            }
        }
        return map;
    }

    public String signDays(String sb,int maxNum,String status){
        String arr[] = sb.split(",");
        StringBuffer buffer = new StringBuffer();
        boolean flag = true;
        int count = 0;
        if(StringUtil.equals(status,"Y")){
            count = arr.length-1;
        }else {
            count = arr.length;
        }
        if(count>0){
            for(int x=0;x<maxNum;x++){
                for(int k=0;k<count;k++){
                    int num = Integer.parseInt(arr[k]);
                    if(num == x+1){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    buffer.append(x+1).append(",");
                }
            }
        }else{
            for(int i=0;i<maxNum;i++){
                buffer.append(i+1).append(",");
            }
        }
        return buffer.length()>0?buffer.deleteCharAt(buffer.length()-1).toString():buffer.toString();
    }
}