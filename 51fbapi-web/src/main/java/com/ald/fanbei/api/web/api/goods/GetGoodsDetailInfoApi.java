package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsDetailInfoVo;
import com.ald.fanbei.api.web.vo.GoodsDetailPicInfoVo;

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
		List<String> goodsPics = new ArrayList<String>();
		List<GoodsDetailPicInfoVo> goodsDetail = new ArrayList<GoodsDetailPicInfoVo>(); 
		vo.setGoodsId(goods.getRid());
		vo.setGoodsIcon(goods.getGoodsIcon());
		vo.setGoodsName(goods.getName());
		vo.setGoodsUrl(goods.getGoodsUrl());
		vo.setNumId(goods.getNumId());
		vo.setOpenId(goods.getOpenId());
		vo.setRealAmount(goods.getRealAmount()+"");
		vo.setRebateAmount(goods.getRebateAmount()+"");
		vo.setSaleAmount(goods.getSaleAmount());
		vo.setSource(goods.getSource());
		vo.setSaleCount(goods.getSaleCount());
		if(StringUtil.isNotBlank(goods.getGoodsDetail())){
			String[] gdArray = goods.getGoodsDetail().split(";");
			if(gdArray!=null && gdArray.length == Constants.GOODSDETAIL_PIC_PARTS){
				GoodsDetailPicInfoVo goodsDetailPicInfoVo = new GoodsDetailPicInfoVo(gdArray[0], gdArray[1], gdArray[2]);
				goodsDetail.add(goodsDetailPicInfoVo);
			}
		}
		vo.setGoodsDetail(goodsDetail);
		//商品图片汇总处理
		if(StringUtil.isNotBlank(goods.getGoodsPic1())){
			goodsPics.add(goods.getGoodsPic1());
		}
		if(StringUtil.isNotBlank(goods.getGoodsPic2())){
			goodsPics.add(goods.getGoodsPic2());
		}
		if(StringUtil.isNotBlank(goods.getGoodsPic3())){
			goodsPics.add(goods.getGoodsPic3());
		}
		if(StringUtil.isNotBlank(goods.getGoodsPic4())){
			goodsPics.add(goods.getGoodsPic4());
		}
		vo.setGoodsPics(goodsPics);
		return vo;
	}
	
}
