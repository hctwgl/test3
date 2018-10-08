package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.JsdESdkService;
import com.ald.fanbei.api.biz.service.JsdUserSealService;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.dao.JsdUserSealDao;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.dal.domain.JsdUserSealDo;
import com.timevale.esign.sdk.tech.bean.result.AddAccountResult;
import com.timevale.esign.sdk.tech.bean.result.AddSealResult;
import com.timevale.esign.sdk.tech.bean.result.GetAccountProfileResult;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


@Service("JsdUserSealService")
public class JsdUserSealServiceImpl implements JsdUserSealService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JsdUserSealServiceImpl.class);

    @Resource
    private JsdUserSealDao jsdUserSealDao;
    @Resource
    private JsdESdkService jsdESdkService;


    @Override
    public JsdUserSealDo getUserSealById(Long id) {
        return jsdUserSealDao.selectById(id);
    }

    @Override
    public JsdUserSealDo getUserSealByUserName(String userName) {
        return jsdUserSealDao.selectByUserName(userName);
    }

    @Override
    public JsdUserSealDo getUserSeal(JsdUserDo afUserDo) {
        JsdUserSealDo afUserSealDo = jsdUserSealDao.selectByUserId(afUserDo.getRid());
        if (null == afUserSealDo) {// 第一次创建个人印章
            afUserSealDo = new JsdUserSealDo();
            Map<String, String> map = new HashMap<>();
            map.put("name", afUserDo.getRealName());
            map.put("idno", afUserDo.getIdNumber());
            map.put("email", afUserDo.getEmail());
            map.put("mobile", afUserDo.getMobile());
            AddAccountResult addAccountResult = getAddAccountResult(afUserDo, map);//创建
            afUserSealDo.setUserAccountId(addAccountResult.getAccountId());
            afUserSealDo.setUserType("2");
            AddSealResult addSealResult = jsdESdkService.createUserSeal(addAccountResult.getAccountId(), "RECTANGLE", "RED");
            if (!addSealResult.isErrShow()) {
                afUserSealDo.setUserId(afUserDo.getRid());
                afUserSealDo.setUserSeal(addSealResult.getSealData());
                // afUserSealDo1.setUserSeal("data:image/png;base64,"+addSealResult.getSealData());
                // userSeal = addSealResult.getSealData();
            }
            int num = insertUserSeal(afUserSealDo);
            return afUserSealDo;
        } else if (null != afUserSealDo.getUserAccountId() && null == afUserSealDo.getUserSeal()) {// 有账户没印章
            AddSealResult addSealResult = jsdESdkService.createUserSeal(afUserSealDo.getUserAccountId().toString(), "SQUARE",
                    "RED");
            if (!addSealResult.isErrShow()) {
                afUserSealDo.setUserId(afUserDo.getRid());
                afUserSealDo.setUserSeal(addSealResult.getSealData());
                afUserSealDo.setUserAccountId(afUserSealDo.getUserAccountId());
                if (afUserDo.getMajiabaoName() != null && "edspay".equals(afUserDo.getMajiabaoName())) {
                    afUserSealDo.setEdspayUserCardId(afUserDo.getIdNumber());
                    afUserSealDo.setUserName(afUserDo.getRealName());
                }
                // userSeal = addSealResult.getSealData();
                int num = jsdUserSealDao.updateByUserId(afUserSealDo);
            }
            return afUserSealDo;
        }
        return afUserSealDo;
    }

    private AddAccountResult getAddAccountResult(JsdUserDo afUserDo, Map<String, String> map) {
        AddAccountResult addAccountResult = jsdESdkService.addUserSealAccount(map);
        if (null != addAccountResult && 150016 == addAccountResult.getErrCode()) {// 账户已存在(该证件号已被使用)
            GetAccountProfileResult getAccountProfileResult = jsdESdkService.getUserSealAccount(map);
            addAccountResult.setAccountId(getAccountProfileResult.getAccountInfo().getAccountUid());
        } else if (null == addAccountResult || !"成功".equals(addAccountResult.getMsg())
                || 0 != addAccountResult.getErrCode()) {
            logger.error("personAccount create error = > {}", addAccountResult.getMsg() + "name=" + afUserDo.getRealName());
            throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        return addAccountResult;
    }

    @Override
    public int insertUserSeal(JsdUserSealDo record) {
        int num = 0;
        try {
            num = jsdUserSealDao.insert(record);
            logger.info("insertUserSeal num => {}", num);
        } catch (Exception e) {
            logger.error("insertUserSeal error => {}", e.getMessage());
        } finally {
        }
        return num;
    }

    @Override
    public int updateUserSealByUserId(JsdUserSealDo record) {
        return jsdUserSealDao.updateByUserId(record);
    }
}
