package com.ald.fanbei.api.web.h5.api.jsd;

import com.ald.fanbei.api.biz.service.JsdUserContactsService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.dal.domain.JsdUserContactsDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 获取通讯录
 */
@Component("getUserContactsInfoApi")
public class GetUserContactsInfoApi implements JsdH5Handle {

    @Resource
    private JsdUserContactsService jsdUserContactsService;


    @Override
    public JsdH5HandleResponse process(Context context) {
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
        String userId = ObjectUtils.toString(context.getData("userId"), null);
        List<Long> userList = new ArrayList<>();
        String arr[] = userId.split(",");
        for(int i=0;i<arr.length;i++){
            String str = arr[i];
            userList.add(Long.valueOf(str.trim()));
        }
        List<JsdUserContactsDo> contacts= jsdUserContactsService.getUserContactsByUserIds(userList);
        Map<String,Object>  data=new HashMap<>();
        List<Map<String,String>> con=new ArrayList<>();
        for (JsdUserContactsDo contactsDo:contacts){
            Map<String,String> map=new HashMap<>();
            map.put(String.valueOf(contactsDo.getUserId()),contactsDo.getContactsMobile());
            map.put("mxreportUrl",contactsDo.getMxreportUrl());
            con.add(map);
        }
        data.put("contacts",con);
        resp.setData(data);
        return resp;
    }
}
