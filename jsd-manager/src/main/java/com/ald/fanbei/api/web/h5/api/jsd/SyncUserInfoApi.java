package com.ald.fanbei.api.web.h5.api.jsd;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.common.enums.GenderType;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;

/**
 * 同步用户信息
 */
@Component("jsdSyncUserInfoApi")
public class SyncUserInfoApi implements JsdH5Handle {

    @Resource
    private JsdUserService jsdUserService;

    @Override
    public JsdH5HandleResponse process(Context context) {
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
        Long userId = context.getUserId();
        String realName = String.valueOf(context.getDataMap().get("realName"));
        String openId = String.valueOf(context.getDataMap().get("userId"));
        String idNumber = String.valueOf(context.getDataMap().get("idNumber"));
        String mobile = String.valueOf(context.getDataMap().get("bankMobile"));
        String address = ObjectUtils.toString(context.getDataMap().get("address"), "");
        String gender = ObjectUtils.toString(context.getDataMap().get("gender"), "");
        if(StringUtil.equals(gender, GenderType.M.getName())){
            gender = GenderType.M.getCode();
        }else if(StringUtil.equals(gender,GenderType.F.getName())){
            gender = GenderType.F.getCode();
        }else {
            gender = GenderType.U.getCode();
        }
        String birthday = ObjectUtils.toString(context.getDataMap().get("birthday"), "");
        JsdUserDo jsdUserDo = new JsdUserDo();
        jsdUserDo.setRealName(realName);
        jsdUserDo.setIdNumber(idNumber);
        jsdUserDo.setMobile(mobile);
        jsdUserDo.setRid(userId);
        jsdUserDo.setOpenId(openId);
        jsdUserDo.setGmtCreate(new Date());
        jsdUserDo.setGmtModified(new Date());
        jsdUserDo.setAddress(address);
        jsdUserDo.setBirthday(birthday);
        jsdUserDo.setGender(gender);
        JsdUserDo userDo = jsdUserService.getById(userId);
        if (userDo == null) {
        	jsdUserService.saveRecord(jsdUserDo);
        }else if(StringUtil.isBlank(userDo.getAddress()) || StringUtil.isBlank(userDo.getBirthday()) || StringUtil.isBlank(userDo.getGender())){
        	jsdUserService.updateById(jsdUserDo);
        }
        return resp;
    }

}