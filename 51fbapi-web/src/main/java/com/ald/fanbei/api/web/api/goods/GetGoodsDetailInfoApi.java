package com.ald.fanbei.api.web.api.goods;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsDetailInfoVo;

/**
 * 
 *@类描述：GetGoodsDetailInfoApi
 *@author 何鑫 2017年3月3日  11:41:32
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getGoodsDetailInfoApi")
public class GetGoodsDetailInfoApi implements ApiHandle{

	@Resource
	private AfGoodsService afGoodsService;
	
	@Resource
	private TaobaoApiUtil taobaoApiUtil;
	
	@Resource
	private AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);
    	AfGoodsDo goods = afGoodsService.getGoodsById(goodsId);
    	if(null == goods){
			throw new FanbeiException("goods not exist error", FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
    	}
    	AfGoodsDetailInfoVo vo = getGoodsVo(goods);
		resp.setResponseData(vo);
		return resp;
	}
	
	private AfGoodsDetailInfoVo getGoodsVo(AfGoodsDo goods){
		AfGoodsDetailInfoVo vo = new AfGoodsDetailInfoVo();
		vo.setGoodsId(goods.getRid());
		vo.setGoodsIcon(goods.getGoodsIcon());
		vo.setGoodsName(goods.getName());
		vo.setGoodsUrl(goods.getGoodsUrl());
		vo.setNumId(goods.getNumId());
		vo.setOpenId(goods.getOpenId());
		vo.setRealAmount(goods.getRealAmount()+"");
		vo.setRebateAmount(goods.getRebateAmount()+"");
		vo.setSaleAmount(goods.getSaleAmount());
		return vo;
	}
	
}
