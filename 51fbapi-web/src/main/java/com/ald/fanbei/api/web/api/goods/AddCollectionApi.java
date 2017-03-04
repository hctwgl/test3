/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfuserCollectionService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserCollectionDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：添加收藏
 * @author suweili 2017年2月25日下午2:03:35
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("addCollectionApi")
public class AddCollectionApi implements ApiHandle {

	@Resource
	AfuserCollectionService afuserCollectionService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> params = requestDataVo.getParams();
		String name = ObjectUtils.toString(params.get("name"), "").toString();
		BigDecimal priceAmount =new  BigDecimal(ObjectUtils.toString(params.get("priceAmount"), "").toString());
		String actualAmount =ObjectUtils.toString(params.get("actualAmount"), "").toString();

		String openId = ObjectUtils.toString(params.get("openId"), "").toString();
		String goodsIcon = ObjectUtils.toString(params.get("goodsIcon"), "").toString();
		String goodsUrl = ObjectUtils.toString(params.get("goodsUrl"), "").toString();

		Long goodsId = NumberUtil.objToLongDefault(params.get("goodsId"), 0);
		if (StringUtils.isEmpty(name) && StringUtils.isEmpty(openId) && StringUtils.isEmpty(goodsIcon)
				&& StringUtils.isEmpty(goodsUrl)) {
			throw new FanbeiException("(name or openId or goodsIcon or goodsUrl ) is  empty", FanbeiExceptionCode.PARAM_ERROR);
		}
		
		
		
		AfUserCollectionDo afUserCollectionDo = new AfUserCollectionDo();
		afUserCollectionDo.setActualAmount(actualAmount);
		afUserCollectionDo.setPriceAmount(priceAmount);
		afUserCollectionDo.setOpenId(openId);
		afUserCollectionDo.setGoodsName(name);
		afUserCollectionDo.setGoodsId(goodsId);
		afUserCollectionDo.setGoodsIcon(goodsIcon);
		afUserCollectionDo.setGoodsUrl(goodsUrl);
		afUserCollectionDo.setUserId(userId);
		if(afuserCollectionService.getUserCollectionCountByGoodsId(afUserCollectionDo)>0){
			throw new FanbeiException("goods alreay collection", FanbeiExceptionCode.GOODS_COLLECTION_ALREADY_EXIST_ERROR);
		}
		if(afuserCollectionService.addUserCollectionGoods(afUserCollectionDo)>0){
			return resp;
		}
        throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
