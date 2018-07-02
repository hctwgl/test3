package com.ald.fanbei.api.web.api.coupon;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.biz.service.AfSubjectGoodsService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;
import com.ald.fanbei.api.dal.domain.AfSubjectGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@Component("getDetailCouponListApi")
public class GetDetailCouponListApi implements ApiHandle {
    @Resource
    AfGoodsService afGoodsService;
    @Resource
    AfUserCouponService afUserCouponService;
    @Resource
    AfSubjectGoodsService afSubjectGoodsService;
    @Resource
    AfUserDao afUserDao;
    @Resource
    AfModelH5ItemService afModelH5ItemService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();

        Long goodsId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("goodsId"), 0);
        List<AfUserCouponDto> list = new ArrayList<AfUserCouponDto>();
        try{
            AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
            Long brandId = afGoodsDo.getBrandId();
            Long categoryId = afGoodsDo.getCategoryId();
            List<AfUserCouponDto> userCouponList = new ArrayList<>();

            List<AfSubjectGoodsDo> subjectGoods = null;
            //获取所有优惠券
            if(userId==null){
                userCouponList = afUserCouponService.getUserAllCoupon();

            }else {
                userCouponList = afUserCouponService.getUserAllCouponByUserId(userId);
            }
            if(userCouponList!=null&&userCouponList.size()>0){
                for(AfUserCouponDto afUserCouponDto:userCouponList){
                    String expiryType = afUserCouponDto.getExpiryType();
                    Date gmtStart = afUserCouponDto.getGmtStart();
                    Date gmtEnd = afUserCouponDto.getGmtEnd();
                    int validDays = afUserCouponDto.getValidDays();
                    int is_global = afUserCouponDto.getIsGlobal();
                    String goodsIds = afUserCouponDto.getGoodsIds();
                    if(StringUtil.isBlank(expiryType)){
                        continue;
                    }else {
                        if(StringUtil.equals("D",expiryType)&&validDays<=0){
                            continue;
                        }else if(StringUtil.equals("R",expiryType)&&(gmtStart==null||gmtEnd==null||gmtEnd.getTime()<=(new Date()).getTime())){
                            continue;
                        }
                    }
                    if(is_global==0){
                        list.add(afUserCouponDto);
                    }else if(is_global==1&&StringUtil.equals("H5_TEMPLATE",afUserCouponDto.getActivityType())&&afUserCouponDto.getActivityId()!=null){
                        Long activityId = afUserCouponDto.getActivityId();
                        List<AfModelH5ItemDo> modelH5ItemList = afModelH5ItemService.getModelH5ItemByGoodsId(goodsId);
                        if(modelH5ItemList!=null&&modelH5ItemList.size()>0){
                            for(AfModelH5ItemDo afModelH5ItemDo : modelH5ItemList) {
                                Long modelId = afModelH5ItemDo.getModelId();
                                if(modelId.equals(activityId)){
                                    list.add(afUserCouponDto);
                                    break;
                                }
                            }
                        }
                    }else if(is_global==2&&StringUtil.isNotBlank(goodsIds)){
                        goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
                        if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(goodsId))){//当前商品id被包含在券信息的商品id集合里面
                            list.add(afUserCouponDto);
                        }
                    }else if(is_global==3&&StringUtil.isNotBlank(goodsIds)){
                        goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
                        if(subjectGoods==null){
                            subjectGoods = afSubjectGoodsService.getSubjectGoodsByGoodsId(goodsId);
                        }
                        if(subjectGoods!=null&&subjectGoods.size()>0){
                            for(AfSubjectGoodsDo afSubjectGoodsDo : subjectGoods){
                                String subjectId = afSubjectGoodsDo.getSubjectId();
                                if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(subjectId))){//当前会场id被包含在券信息的会场id集合里面
                                    list.add(afUserCouponDto);
                                    break;
                                }
                            }
                        }
                    }else if(is_global==4&&StringUtil.isNotBlank(goodsIds)){
                        goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
                        if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(categoryId))){//当前分类id被包含在券信息的分类id集合里面
                            list.add(afUserCouponDto);
                        }
                    }else if(is_global==5&&StringUtil.isNotBlank(goodsIds)){
                        goodsIds = goodsIds.replaceAll("，",",");//将字符串中中文的逗号替换成英文的逗号
                        if(Arrays.asList(goodsIds.split(",")).contains(String.valueOf(brandId))){//当前品牌id被包含在券信息的品牌id集合里面
                            list.add(afUserCouponDto);
                        }
                    }
                }
            }
        }catch (Exception e){
            logger.error("getCouponList error for " + e);
        }
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("couponList", JSON.toJSON(list));
        resp.setResponseData(data);
        return resp;
    }
}
