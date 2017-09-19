package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfUnionLoginLogDao;
import com.ald.fanbei.api.dal.domain.AfUnionLoginLogDo;
import com.ald.fanbei.api.biz.service.AfUnionLoginLogService;

import java.util.Date;


/**
 * '联合登录用户登录日志表ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-19 15:33:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUnionLoginLogService")
public class AfUnionLoginLogServiceImpl extends ParentServiceImpl<AfUnionLoginLogDo, Long> implements AfUnionLoginLogService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfUnionLoginLogServiceImpl.class);
   
    @Resource
    private AfUnionLoginLogDao afUnionLoginLogDao;

		@Override
	public BaseDao<AfUnionLoginLogDo, Long> getDao() {
		return afUnionLoginLogDao;
	}

    @Override
    public void addLog(String channel, String phone, String paramsJsonStr) {
		    try{
                AfUnionLoginLogDo afUnionLoginLogDo=new AfUnionLoginLogDo();
                afUnionLoginLogDo.setChannelCode(channel);
                afUnionLoginLogDo.setGmtModified(new Date());
                afUnionLoginLogDo.setPhone(phone);
                afUnionLoginLogDo.setRequestInfo(paramsJsonStr);
                saveRecord(afUnionLoginLogDo) ;
            }catch (Exception e){
		        logger.error("addLog:",e);
            }

    }
}