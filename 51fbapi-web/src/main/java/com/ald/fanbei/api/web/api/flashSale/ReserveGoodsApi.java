package com.ald.fanbei.api.web.api.flashSale;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @类描述：商品分类
 * @author chefeipeng 2017年10月25日下午2:03:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("reserveGoodsApi")
public class ReserveGoodsApi implements ApiHandle {

    @Resource
    AfGoodsService afGoodsService;
    @Resource
    AfUserGoodsSmsService afUserGoodsSmsService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        Long goodsId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("goodsId"),0l);
        String goodsName = ObjectUtils.toString(requestDataVo.getParams().get("goodsName"), "").toString();
        AfGoodsDo afGoodsDo = afGoodsService.getGoodsById(goodsId);
        if(null != afGoodsDo){
            goodsName = afGoodsDo.getName();
        }
        AfUserGoodsSmsDo afUserGoodsSmsDo = new AfUserGoodsSmsDo();
        afUserGoodsSmsDo.setGoodsId(goodsId);
        afUserGoodsSmsDo.setUserId(userId);
        afUserGoodsSmsDo.setIsDelete(0l);
        afUserGoodsSmsDo.setGoodsName(goodsName);
        AfUserGoodsSmsDo afUserGoodsSms = afUserGoodsSmsService.selectByGoodsIdAndUserId(afUserGoodsSmsDo);
        if(null != afUserGoodsSms){
            throw new FanbeiException(FanbeiExceptionCode.GOODS_HAVE_BEEN_RESERVED);
        }
        try{
            int flag = afUserGoodsSmsService.insertByGoodsIdAndUserId(afUserGoodsSmsDo);
            if(flag <= 0){
                throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
            }
        }catch (Exception e){
            throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
        }
        return resp;
    }
}
