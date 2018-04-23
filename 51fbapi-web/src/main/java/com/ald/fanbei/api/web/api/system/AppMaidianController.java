package com.ald.fanbei.api.web.api.system;
        import java.io.IOException;

        import javax.servlet.http.HttpServletRequest;

        import com.ald.fanbei.api.web.common.*;
        import org.apache.commons.lang.ObjectUtils;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.ModelMap;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RequestMethod;
        import org.springframework.web.bind.annotation.ResponseBody;

        import com.ald.fanbei.api.common.FanbeiContext;
        import com.ald.fanbei.api.common.exception.FanbeiException;
        import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
        import com.alibaba.fastjson.JSON;
        import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 返场活动
 * @author 江荣波 2017年6月20日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-app")
public class AppMaidianController implements ApiHandle {

    @RequestMapping(value = "postMaidianInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        try {
            //doCheck(request, false);
            String maidianInfo = ObjectUtils.toString(request.getParameter("maidianInfo"), "").toString();
            String maidianInfo1 = ObjectUtils.toString(request.getParameter("maidianInfo1"), "").toString();
            String maidianInfo2 = ObjectUtils.toString(request.getParameter("maidianInfo2"), "").toString();
            String maidianInfo3 = ObjectUtils.toString(request.getParameter("maidianInfo3"), "").toString();

            // 获取埋点标识
            H5CommonResponse resp = H5CommonResponse.getNewInstance(true,"","",new ModelMap());
            BaseController.doMaidianLog(request, resp, maidianInfo, maidianInfo1, maidianInfo2, maidianInfo3);
            ApiHandleResponse respApp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
            return respApp;
        } catch (Exception e) {
            logger.error("fanbei-app postMaidianInfo error:",e);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
        }
    }


}