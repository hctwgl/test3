/**
 *
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author chenqiwei 2017年11月23日下午15:17:22
 * @类描述：引流页轮播图
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getDrainageBannerListApi")
public class GetDrainageBannerListApi implements ApiHandle {

    @Resource
    AfResourceService afResourceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String resourceType = ObjectUtils.toString(requestDataVo.getParams().get("type"), "").toString();
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        Integer appVersion = NumberUtil.objToInteger(requestDataVo.getSystem().get("appVersion"));
        boolean isIos = requestDataVo.getId().startsWith("i");
        logger.info("getDrainageBannerListApi and type = {}", type);
        List<AfResourceDo> bannerList1 = new ArrayList<AfResourceDo>();
        //线上为开启状态
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
            bannerList1 = afResourceService.getResourceHomeListByTypeOrderBy(resourceType);
        } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
            //预发不区分状态
            bannerList1 = afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(resourceType);
        }
        logger.info("getDrainageBannerListApi and bannerList1 = {}", bannerList1);
        List<Object> bannerList = getObjectWithResourceList(bannerList1,appVersion,isIos);
        resp.addResponseData("bannerList", bannerList);

        return resp;
    }

    private List<Object> getObjectWithResourceList(List<AfResourceDo> bannerResclist,Integer appVersion,boolean isIos) {
        List<Object> bannerList = new ArrayList<Object>();
        if(CollectionUtils.isNotEmpty(bannerResclist)){
            for (AfResourceDo afResourceDo : bannerResclist) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("imageUrl", afResourceDo.getValue());
                data.put("titleName", afResourceDo.getName());
                data.put("type", afResourceDo.getValue1());
                data.put("content", afResourceDo.getValue2());
                data.put("sort", afResourceDo.getSort());
                data.putAll(buildParam(afResourceDo,appVersion,isIos));
                bannerList.add(data);
            }
        }
        return bannerList;
    }

    private Map<String,Object> buildParam(AfResourceDo afResourceDo,Integer appVersion,boolean isIos){
        Map<String, Object> data = new HashMap<String, Object>();
        if(appVersion >= 408) {
            if (isIos) {
                String param = afResourceDo.getValue3();
                data.put("className", param.split(",")[0]);
                data.put("createType", param.split(",")[1]);
                data.put("needLogin", param.split(",")[2]);
                data.put("paramDic", param.split(",")[3]);
                data.put("jumpType", param.split(",")[4]);
            }else{
                String param = afResourceDo.getValue4();
                data.put("className", param.split(",")[0]);
                data.put("createType", param.split(",")[1]);
                data.put("needLogin", param.split(",")[2]);
                data.put("paramDic", param.split(",")[3]);
            }
        }else{
            data.put("className", null);
            data.put("createType", null);
            data.put("needLogin", null);
            data.put("paramDic", null);
            data.put("jumpType", null);
        }
        return data;
    }


}
