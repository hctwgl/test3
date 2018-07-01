package com.ald.fanbei.api.web.h5.api.dsed;

import com.ald.fanbei.api.biz.service.DsedUserContactsService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedUserContactsDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取通讯录
 */
@Component("getUserContactsInfoApi")
public class GetUserContactsInfoApi implements DsedH5Handle {

    @Resource
    private DsedUserContactsService dsedUserContactsService;

    @Resource
    private DsedUserService dsedUserService;

    @Override
    public DsedH5HandleResponse process(Context context) {
        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        String userId = ObjectUtils.toString(context.getData("userId"), null);
        List<DsedUserContactsDo> contacts= dsedUserContactsService.getUserContactsByUserId(String.valueOf(userId));
        Map<String,Object>  data=new HashMap<>();
        data.put("contacts",contacts.get(0).getContactsMobile());
        resp.setData(data);
        return resp;
    }
}
