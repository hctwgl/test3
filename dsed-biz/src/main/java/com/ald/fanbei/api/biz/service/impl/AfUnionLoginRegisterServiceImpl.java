package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUnionLoginRegisterService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfUnionLoginRegisterDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfUnionLoginRegisterDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;


/**
 * '联合登录用户登录日志表ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-09-19 15:36:07
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afUnionLoginRegisterService")
public class AfUnionLoginRegisterServiceImpl extends ParentServiceImpl<AfUnionLoginRegisterDo, Long> implements AfUnionLoginRegisterService {

    protected final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");//第三方调用日志

    @Autowired
    AfUserService afUserService;
    @Resource
    private AfUnionLoginRegisterDao afUnionLoginRegisterDao;

		@Override
	public BaseDao<AfUnionLoginRegisterDo, Long> getDao() {
		return afUnionLoginRegisterDao;
	}

    @Override
    public String register(String channel,String phone,String paramsJsonStr) throws Exception {
        String salt = UserUtil.getSalt();
        String defaultPassword= CommonUtil.getRandomNumber(6);
        String randomPassword = DigestUtil.MD5(defaultPassword);

        String password = UserUtil.getPassword(randomPassword, salt);
        AfUserDo userDo = new AfUserDo();
        userDo.setSalt(salt);
        userDo.setUserName(phone);
        userDo.setMobile(phone);
        userDo.setNick("");
        userDo.setPassword(password);
        userDo.setRecommendId(0l);
        long userId=0;
        long i = afUserService.addUser(userDo);
        if (i > 0) {
            userId = userDo.getRid();
            AfUnionLoginRegisterDo afUnionLoginRegisterDo=new AfUnionLoginRegisterDo();
            afUnionLoginRegisterDo.setChannelCode(channel);
            afUnionLoginRegisterDo.setDefaultPassword(password);
            afUnionLoginRegisterDo.setGmtCreate(new Date());
            afUnionLoginRegisterDo.setRequestInfo(paramsJsonStr.substring(0,paramsJsonStr.length()>1024?1024:paramsJsonStr.length()));
            afUnionLoginRegisterDo.setPhone(phone);
            afUnionLoginRegisterDo.setUserId(userId);
            saveRecord(afUnionLoginRegisterDo) ;
            //新增用户成功后，生成邀请码，并更新用户记录
            try{
                    Long invteLong = Constants.INVITE_START_VALUE + userId;
        	    String inviteCode = Long.toString(invteLong, 36);
        	    userDo.setRecommendCode(inviteCode);
        	    userDo.setRid(userId);
        	    afUserService.updateUser(userDo);
                }catch(Exception e){
                    thirdLog.error("更新邀请码失败！" + phone + e);
            }
        } else {
            thirdLog.error("新增用户失败！phone ：" + phone);
            throw new Exception("新增用户失败");
        }
        return defaultPassword;
    }
}