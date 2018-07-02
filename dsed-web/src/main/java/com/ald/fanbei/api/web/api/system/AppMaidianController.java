package com.ald.fanbei.api.web.api.system;

import com.ald.fanbei.api.biz.service.impl.MaidianRunnable;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.dto.AppMaidianDto;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @类描述：
 * 返场活动
 * @author 江荣波 2017年6月20日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-app")
public class AppMaidianController{

    Logger logger = LoggerFactory.getLogger(AppMaidianController.class);

    @Autowired
    ThreadPoolTaskExecutor threadPoolMaidianTaskExecutor;

    @ResponseBody
    @RequestMapping(value = "postMaidianInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public ApiHandleResponse process(@RequestBody AppMaidianDto appMaidianDto, HttpServletRequest request) {
        try {
            //logger.info("fanbei-app postMaidianInfo:"+appMaidianDto.toString()+", request:" + request.toString());

            String maidianInfo = ObjectUtils.toString(appMaidianDto.getMaidianInfo(), "").toString();
            String maidianInfo1 ="";
            if(StringUtils.isNotBlank(request.getHeader("id"))) {
                maidianInfo1 = ObjectUtils.toString(request.getHeader("id").substring(0,1), "").toString();
            }
            String maidianInfo2 = ObjectUtils.toString(appMaidianDto.getMaidianInfo2(), "").toString();
            String maidianInfo3 = ObjectUtils.toString(appMaidianDto.getMaidianInfo3(), "").toString();

            if(StringUtils.isBlank(maidianInfo) || StringUtils.isBlank(maidianInfo3))
            {
                logger.error("app postMaidianInfo params error:"+appMaidianDto.toString());
                //return new ApiHandleResponse(request.getHeader("id"),  FanbeiExceptionCode.SYSTEM_ERROR,"埋点参数异常");
            }

            MaidianRunnable maidianRunnable = new MaidianRunnable(request, "",true, maidianInfo, maidianInfo1, maidianInfo2, maidianInfo3);
            threadPoolMaidianTaskExecutor.execute(maidianRunnable);
            return new ApiHandleResponse(request.getHeader("id"), FanbeiExceptionCode.SUCCESS);
        } catch (Exception e) {
            logger.error("fanbei-app postMaidianInfo error:", e);
            return new ApiHandleResponse(request.getHeader("id"), FanbeiExceptionCode.SYSTEM_ERROR);
        }
    }


}