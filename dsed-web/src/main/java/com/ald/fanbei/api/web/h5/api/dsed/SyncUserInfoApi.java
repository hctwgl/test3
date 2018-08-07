package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
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
        String address = String.valueOf(context.getDataMap().get("address"));
        String gender = String.valueOf(context.getDataMap().get("gender"));
        String birthday = String.valueOf(context.getDataMap().get("birthday"));
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
        }
        if(StringUtil.isBlank(userDo.getAddress()) || StringUtil.isBlank(userDo.getBirthday()) || StringUtil.isBlank(userDo.getGender())){
            dsedUserService.updateById(dsedUserDo);
        }

        return resp;
    }

}