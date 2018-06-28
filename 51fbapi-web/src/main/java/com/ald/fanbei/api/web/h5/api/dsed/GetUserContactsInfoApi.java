package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.DsedUserContactsService;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedUserContactsDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 获取通讯录
 */
@Component("getUserContactsInfoApi")
public class GetUserContactsInfoApi implements DsedH5Handle {

    @Resource
    private DsedUserContactsService dsedUserContactsService;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        String userId = ObjectUtils.toString(context.getData("userId"), null);
        List<DsedUserContactsDo> userContactsDoList= dsedUserContactsService.getUserContactsByUserId(userId);
        resp.setData(userContactsDoList);
        return resp;
    }
}
