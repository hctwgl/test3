/**
 *
 */
package com.ald.fanbei.api.web.api.agencybuy;

import com.ald.fanbei.api.biz.bo.AfTradeRebateModelBo;
import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTradeBusinessInfoService;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfTradeBusinessInfoDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author suweili 2017年5月29日下午3:50:12
 * @类描述：获取代买分期信息
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("GetTradeNperInfoApi")
public class GetTradeNperInfoApi implements ApiHandle {
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfTradeBusinessInfoService afTradeBusinessInfoService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long businessId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("businessId"), 1l);
        Map<String, Object> params = requestDataVo.getParams();
        BigDecimal amount = NumberUtil.objToBigDecimalDefault(params.get("amount"), BigDecimal.ZERO);
        //region 没有配置就采用默认值
        JSONArray rebateModels =new JSONArray();
        //#endregion
        if( context.getAppVersion()>390){
            AfTradeBusinessInfoDo afTradeBusinessInfoDo = afTradeBusinessInfoService.getByBusinessId(businessId);
            String configRebateModel = afTradeBusinessInfoDo.getConfigRebateModel();
            if (StringUtils.isNotBlank(configRebateModel)) {
                try{
                    rebateModels=JSON.parseArray(configRebateModel);
                }catch (Exception e){
                    logger.info( "GetTradeNperInfoApi process error",e.getCause());
                }

            }
        }
        //获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_TRADE);
        JSONArray array = JSON.parseArray(resource.getValue());




        List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array,  rebateModels, BigDecimal.ONE.intValue(),
                amount, resource.getValue1(), resource.getValue2(),0l,"0");

        resp.addResponseData("nperList", nperList);

        return resp;
    }

}
