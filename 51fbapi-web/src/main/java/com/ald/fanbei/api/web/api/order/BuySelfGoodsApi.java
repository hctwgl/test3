/**
 * 
 */
package com.ald.fanbei.api.web.api.order;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：自营商品下单（oppr11）
 * @author suweili 2017年6月16日下午3:44:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("buySelfGoodsApi")
public class BuySelfGoodsApi implements ApiHandle {

	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId"),""), 0l);
        Integer count = NumberUtil.objToIntDefault(requestDataVo.getParams().get("count"), 1);
        Integer nper = NumberUtil.objToIntDefault(requestDataVo.getParams().get("nper"), 0);
        AfGoodsDo goodsDo =  afGoodsService.getGoodsById(goodsId);
        if(goodsDo ==null){
			throw new FanbeiException(FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
        }
        AfOrderDo afOrder = new AfOrderDo();
    	
		afOrder.setUserId(userId);
		afOrder.setActualAmount(goodsDo.getSaleAmount().max(new BigDecimal(count)));
		afOrder.setSaleAmount(goodsDo.getSaleAmount());
		afOrder.setPriceAmount(goodsDo.getPriceAmount());
		afOrder.setGoodsIcon(goodsDo.getGoodsIcon());
		afOrder.setGoodsName(goodsDo.getName());
		afOrder.setCount(count);
		afOrder.setNper(nper);
		if(nper.intValue()>0){
			//保存手续费信息
			JSONObject borrowRate = afResourceService.borrowRateWithResource(nper);
		}
		
        return resp;
	}

}
