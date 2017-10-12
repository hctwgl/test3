package com.ald.fanbei.api.web.api.goods;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName GetThirdGoodsDetailInfo.
 * @desc
 * 获取第三方自建商品详情
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/12 15:52
 */
@Component("getThirdGoodsDetailInfoApi")
public class GetThirdGoodsDetailInfo implements ApiHandle {
    
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        return null;
    }
}
