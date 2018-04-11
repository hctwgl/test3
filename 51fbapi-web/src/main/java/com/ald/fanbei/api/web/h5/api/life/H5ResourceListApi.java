package com.ald.fanbei.api.web.h5.api.life;

import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import org.springframework.stereotype.Component;

/**
 * 查找生活首页的H5资源列表
 *
 * @author wangli
 * @date 2018/4/10 19:36
 */
@Component("h5ResourceListApi")
public class H5ResourceListApi implements H5Handle {





    @Override
    public H5HandleResponse process(Context context) {
        /*H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);


        resp.setResponseData();

        return resp;*/
        return null;
    }



}
