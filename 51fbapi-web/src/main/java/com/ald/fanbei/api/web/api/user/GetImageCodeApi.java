package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ImageUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * @author 沈铖 2017/7/3 下午1:54
 * @类描述: GetImageCode
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getImageCodeApi")
public class GetImageCodeApi implements ApiHandle {

    @Resource
    private BizCacheUtil bizCacheUtil;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = null;
        try {
            //获得图片验证码
            Map<String, BufferedImage> map = ImageUtil.createRandomImage();
            if (map.size() == 1) {
                String code = "";
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                for (Map.Entry<String, BufferedImage> entry : map.entrySet()) {
                    code = entry.getKey();
                    ImageIO.write(entry.getValue(), "jpeg", byteArrayOutputStream);
                }
                if (StringUtil.isNotBlank(code)) {
                    resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
                    byte[] imageByteArray = byteArrayOutputStream.toByteArray();
                    String image = Base64.encodeBytes(imageByteArray);
                    resp.addResponseData("imageString", image);

                    //将验证码放入redis
                    String id = requestDataVo.getId().split("_")[1];
                    bizCacheUtil.saveObject(id, code, 30 * 60l);
                }
            }
        } catch (Exception e) {
            logger.error("getImageCode is error", e);
        }
        if(resp == null) {
            resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
        }
        return resp;
    }
}
