package com.ald.fanbei.api.web.api.goods;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfSeckillActivityDto;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfGoodsSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsDetailInfoVo;
import com.ald.fanbei.api.web.vo.GoodsDetailPicInfoVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	AfActivityGoodsService afActivityGoodsService;
	@Resource
	private AfResourceService afResourceService;
	
	@Resource
	private AfSchemeGoodsService afSchemeGoodsService;
	
	@Resource
	private AfInterestFreeRulesService afInterestFreeRulesService;

	@Resource
	private AfSeckillActivityService afSeckillActivityService;

	@Resource
	BizCacheUtil bizCacheUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);
    	AfGoodsDo goods = afGoodsService.getGoodsById(goodsId);
    	if(null == goods){
			throw new FanbeiException("goods not exist error", FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
    	}
    	//获取借款分期配置信息
        AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
        JSONArray array = JSON.parseArray(resource.getValue());
        //删除2分期
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
        //removeSecondNper(array);
        
    	BigDecimal saleAmount = goods.getSaleAmount();
    	AfSchemeGoodsDo schemeGoodsDo = null;
		try {
			schemeGoodsDo = afSchemeGoodsService.getSchemeGoodsByGoodsId(goodsId);
		} catch(Exception e){
			logger.error(e.toString());
		}
		JSONArray interestFreeArray = null;
		if(schemeGoodsDo != null){
			AfInterestFreeRulesDo  interestFreeRulesDo = afInterestFreeRulesService.getById(schemeGoodsDo.getInterestFreeId());
			String interestFreeJson = interestFreeRulesDo.getRuleJson();
			if (StringUtils.isNotBlank(interestFreeJson) && !"0".equals(interestFreeJson)) {
				interestFreeArray = JSON.parseArray(interestFreeJson);
			}
		}
		List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, interestFreeArray, BigDecimal.ONE.intValue(),
				saleAmount, resource.getValue1(), resource.getValue2(),goodsId);
		AfGoodsDetailInfoVo vo = getGoodsVo(goods);
		if(nperList!= null){
			Map nperMap = nperList.get(nperList.size() - 1);
			vo.setNperMap(nperMap);
		}
		//秒杀、促销活动商品信息
		AfSeckillActivityDto afSeckillActivityDto = afSeckillActivityService.getActivityByGoodsId(goodsId);
		if(afSeckillActivityDto!=null){
			Long activityId = afSeckillActivityDto.getRid();
			Date gmtStart = afSeckillActivityDto.getGmtStart();
			Date gmtEnd = afSeckillActivityDto.getGmtEnd();
			vo.setActivityId(activityId);
			vo.setActivityType(afSeckillActivityDto.getType());
			vo.setActivityName(afSeckillActivityDto.getName());
			vo.setGmtStart(gmtStart);
			vo.setGmtEnd(gmtEnd);
			vo.setGmtPstart(afSeckillActivityDto.getGmtPStart());
			vo.setLimitCount(afSeckillActivityDto.getLimitCount());
			/*List<AfSeckillActivityGoodsDo> activityGoodsDos = afSeckillActivityService.getActivityGoodsByGoodsId(goodsId);
			for (int i=0;i<activityGoodsDos.size();i++){
				AfSeckillActivityGoodsDo afSeckillActivityGoodsDo = activityGoodsDos.get(i);
				Long actPriceId = afSeckillActivityGoodsDo.getPriceId();
				int limitCount = afSeckillActivityGoodsDo.getLimitCount();
				String lockKey = Constants.CACHEKEY_SECKILL_PRICE + activityId + "+" + actPriceId;
				if(bizCacheUtil.getObject(lockKey)==null){
					bizCacheUtil.saveObject(lockKey,limitCount, DateUtil.getNumberOfMinuteBetween(new Date(),gmtEnd)*60);
				}
			}*/

		}

		resp.setResponseData(vo);
		return resp;
	}
	
    private void removeSecondNper(JSONArray array) {
        if (array == null) {
            return;
        }
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {//mark
                it.remove();
                break;
            }
        }
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
		/* 商品详情图：多图，用英文逗号隔开  by weiqingeng
		 * 1: 图片没有尺寸 http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg,http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg
		 * 2: 图片有尺寸 http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg;1024;760,http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg;1024;760
		*/
		if(StringUtil.isNotBlank(goods.getGoodsDetail())){
			String[] details = goods.getGoodsDetail().split(",");
			if(null != details && details.length > 0){
				for(String detail : details){
					String[] gdArray = detail.split(";");
					if(gdArray != null && gdArray.length > 0){
						if(gdArray.length == Constants.GOODSDETAIL_PIC_PARTS){
							GoodsDetailPicInfoVo goodsDetailPicInfoVo = new GoodsDetailPicInfoVo(gdArray[0], gdArray[1], gdArray[2]);
							goodsDetail.add(goodsDetailPicInfoVo);
						}else{
							GoodsDetailPicInfoVo goodsDetailPicInfoVo = new GoodsDetailPicInfoVo(gdArray[0], null, null);
							goodsDetail.add(goodsDetailPicInfoVo);
						}
					}
				}
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
		//如果为自营商品，numId设置为goodsId
		if(AfGoodsSource.SELFSUPPORT.getCode().equals(goods.getSource())){
			vo.setNumId(goods.getRid()+"");
			//是否限购
			AfActivityGoodsDo afActivityGoodsDo = afActivityGoodsService.getActivityGoodsByGoodsIdAndType(goods.getRid());
			if(null != afActivityGoodsDo){
				if(StringUtils.isEmpty(afActivityGoodsDo.getLimitCount()+"") || (afActivityGoodsDo.getLimitCount() == 0)){
					vo.setLimitedPurchase(-1);
				}else{
					vo.setLimitedPurchase(new Long(afActivityGoodsDo.getLimitCount()).intValue());
				}
			}else{
				vo.setLimitedPurchase(-1);
			}
		}else{
			vo.setLimitedPurchase(-1);
		}
		return vo;
	}

}
