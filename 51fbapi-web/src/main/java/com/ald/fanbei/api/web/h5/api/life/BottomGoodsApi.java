package com.ald.fanbei.api.web.h5.api.life;

import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 查找生活首页底部商品
 *
 * @author wangli
 * @date 2018/4/11 8:07
 */
@Component("bottomGoodsApi")
public class BottomGoodsApi implements H5Handle {





    @Autowired
    private AfResourceService afResourceService;

    @Autowired
    private AfResourceH5ItemService afResourceH5ItemService;



    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);

       /* try {

            resp.setResponseData(data);
            return resp;
        } catch (FanbeiException e) {
            return new H5HandleResponse(context.getId(), e.getErrorCode());
        }*/
       return  null;
    }


}
