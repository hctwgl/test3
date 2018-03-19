/**
 *
 */
package com.ald.fanbei.api.web.api.brand;

import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.enums.ResourceFromEnum;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
 * @author chenqiwei
 * @author weiqingeng
 * @date 2017年11月23日下午15:17:22
 * @类描述：引流页轮播图
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getDrainageBannerListApi")
public class GetDrainageBannerListApi implements ApiHandle {
    @Resource
    AfResourceService afResourceService;

    /**
     *
     营销管理】-【轮播管理】
     1.BORROW_MONEY_BANNER  借钱还款结果页
     2.INSTALLMENT_PAYMENT_BANNER 分期账单还款结果页
     3.BORROW_FINISH_BANNER 借款完成页
     4.BILLING_DETAIL_BANNER 账单详情页

     【营销管理】-【专场管理】
     5.NO_PAYMENT_BANNER 未出账单列表页
     6.YES_PAYMENT_BANNER 已出账单列表页
     */
    private final String[] RESOURCE_TYPES = new String[]{"BORROW_MONEY_BANNER","INSTALLMENT_PAYMENT_BANNER","BORROW_FINISH_BANNER","BILLING_DETAIL_BANNER","NO_PAYMENT_BANNER","YES_PAYMENT_BANNER"};

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String resourceType = ObjectUtils.toString(requestDataVo.getParams().get("type"), "").toString();
        String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        Integer appVersion = NumberUtil.objToInteger(requestDataVo.getSystem().get("appVersion"));
        logger.info("getDrainageBannerListApi and type = {}", type);
        List<Object> resultList = new ArrayList<Object>();
        if(appVersion >= 408 && Arrays.asList(RESOURCE_TYPES).contains(resourceType)) {//新逻辑，新轮播图和新专场(未出账单列表页和已出账单列表页)
            boolean isIos = requestDataVo.getId().startsWith("i");
            String from = ObjectUtils.toString(requestDataVo.getParams().get("from"));// 1:banner
            resultList = doNewProcess(type,resourceType,isIos,from);
        }else{//老专场走老逻辑
            resultList = doOldProcess(type,resourceType);
        }
        resp.addResponseData("bannerList", resultList);
        return resp;
    }


    /**
     * 处理新逻辑
     * @author weiqingeng
     * @return
     */
    private List<Object> doNewProcess(String type, String resourceType,boolean isIos,String from){
        List<Object> resultList = new ArrayList<Object>();
        List<AfResourceDo> bannerList = new ArrayList<AfResourceDo>();
        if(from.equals(ResourceFromEnum.SPECIAL.getCode())){//专场
            bannerList = afResourceService.getNewSpecialResource(resourceType);
        }else{
            //线上为开启状态
            if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
                bannerList = afResourceService.getResourceHomeListByTypeOrderBy(resourceType);
            } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
                //预发不区分状态
                bannerList = afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(resourceType);
            }
            logger.info("getDrainageBannerListApi and bannerList1 = {}", bannerList);
        }
        resultList = getObjectWithResourceList(bannerList,false,isIos);
        return resultList;

    }

    /**
     * 处理老逻辑
     * @author weiqingeng
     * @return
     */
    private List<Object> doOldProcess(String type, String resourceType){
        List<AfResourceDo> bannerList = new ArrayList<AfResourceDo>();
        //线上为开启状态
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
            bannerList = afResourceService.getResourceHomeListByTypeOrderBy(resourceType);
        } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) {
            //预发不区分状态
            bannerList = afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(resourceType);
        }
        logger.info("doOldProcess = {}", bannerList);
        List<Object> resultList = getObjectWithResourceList(bannerList,true,false);
        return resultList;

    }

    /**
     * 数据整理
     * @param bannerResclist
     * @param isOldVersion
     * @param isIos
     * @return
     */
    private List<Object> getObjectWithResourceList(List<AfResourceDo> bannerResclist,boolean isOldVersion,boolean isIos) {
        List<Object> bannerList = new ArrayList<Object>();
        if(CollectionUtils.isNotEmpty(bannerResclist)){
            for (AfResourceDo afResourceDo : bannerResclist) {
                Map<String, Object> data = new HashMap<String, Object>();
                data.put("imageUrl", afResourceDo.getValue());
                data.put("titleName", afResourceDo.getName());
                data.put("type", afResourceDo.getValue1());
                data.put("content", afResourceDo.getValue2());
                data.put("sort", afResourceDo.getSort());
                Map<String, Object> autoParam = buildParam(afResourceDo,isOldVersion,isIos);
                data.putAll(autoParam);
                bannerList.add(data);
            }
        }
        return bannerList;
    }

    /**
     * 包装客户端的动态参数
     * @param afResourceDo
     * @param isOldVersion
     * @param isIos
     * @return
     */
    private Map<String,Object> buildParam(AfResourceDo afResourceDo,boolean isOldVersion,boolean isIos){
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            if(!isOldVersion) {
                String jsonParam = afResourceDo.getValue5();
                if(StringUtils.isNotBlank(jsonParam)){
                    JSONObject json = JSONObject.parseObject(jsonParam);
                    data.put("createType", json.get("createType"));
                    data.put("needLogin", json.get("needLogin"));
                    data.put("paramDic", json.getJSONObject("paramDic"));
                    data.put("jumpType", json.get("jumpType"));
                    data.put("needParam", json.get("needParam"));
                    if (isIos) {
                        data.put("className", json.getString("classNameIOS"));
                    }else{
                        data.put("className", json.getString("classNameAndroid"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


}
