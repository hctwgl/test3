package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.common.enums.GenderType;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 获取借钱首页信息
 *
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.GetLegalBorrowCashHomeInfoV2Api}
 */
@Component("dsedSyncUserInfoApi")
public class SyncUserInfoApi implements DsedH5Handle {

    @Resource
    private DsedUserService dsedUserService;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
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
        DsedUserDo dsedUserDo = new DsedUserDo();
        dsedUserDo.setRealName(realName);
        dsedUserDo.setIdNumber(idNumber);
        dsedUserDo.setMobile(mobile);
        dsedUserDo.setRid(userId);
        dsedUserDo.setOpenId(openId);
        dsedUserDo.setGmtCreate(new Date());
        dsedUserDo.setGmtModified(new Date());
        dsedUserDo.setAddress(address);
        dsedUserDo.setBirthday(birthday);
        dsedUserDo.setGender(gender);
        DsedUserDo userDo = dsedUserService.getById(userId);
        if (userDo == null) {
            dsedUserService.saveRecord(dsedUserDo);
        }else if(StringUtil.isBlank(userDo.getAddress()) || StringUtil.isBlank(userDo.getBirthday()) || StringUtil.isBlank(userDo.getGender())){
            dsedUserService.updateById(dsedUserDo);
        }


        return resp;
    }

}