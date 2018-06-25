package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 获取借钱首页信息
 *
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.GetLegalBorrowCashHomeInfoV2Api}
 */
@Component("dsedSyncUserInfoApi")
public class SyncUserInfoApi implements H5Handle {

    @Resource
    private DsedUserService dsedUserService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String realName = String.valueOf(context.getData("realName"));
        String openId = String.valueOf(context.getData("openId"));
        String idNumber = String.valueOf(context.getData("idNumber"));
        String mobile = String.valueOf(context.getData("mobile"));
        DsedUserDo dsedUserDo = new DsedUserDo();
        dsedUserDo.setRealName(realName);
        dsedUserDo.setIdNumber(idNumber);
        dsedUserDo.setMobile(mobile);
        dsedUserDo.setRid(userId);
        dsedUserDo.setOpenId(openId);
        DsedUserDo userDo = dsedUserService.getById(userId);
        if (userDo == null) {
            dsedUserService.saveRecord(dsedUserDo);
        }
        return resp;
    }

}