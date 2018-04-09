package com.ald.fanbei.api.web.h5.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShareGoodsService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.AfShareGoodsDto;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfFreshmanGoodsVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @ClassName: H5FreshmanShareController
 * @Description: 新用户专享商品 H5
 * @author yanghailong
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 *
 */
@RestController
@RequestMapping(value = "/activity/freshmanShareH5", produces = "application/json;charset=UTF-8")
public class H5FreshmanShareController extends H5Controller{
	
	
	@Resource
	AfUserService afUserService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfShareGoodsService afShareGoodsService;
	@Resource
	AfResourceService afResourceService;
	
	/**
	 * 
	 * @Title: share
	 * @Description: 新人专享商品列表   主页面
	 * @return  String  
	 * @author yanghailong
	 * @data  2017年11月3日
	 */
	@RequestMapping(value = "/homePage", method = RequestMethod.POST)
	public String homePage(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		String result = "";
		try {
			doWebCheck(request, false);
			
			List<AfShareGoodsDto> shareGoods = afShareGoodsService.getShareGoods();
			
			List<AfFreshmanGoodsVo> resultList = new ArrayList<AfFreshmanGoodsVo>();
			
			if(shareGoods!=null){
				
				for (AfShareGoodsDto afShareGoodsDto : shareGoods) {
					AfFreshmanGoodsVo afFreshmanGoodsVo = new AfFreshmanGoodsVo();
					afFreshmanGoodsVo.setNumId(String.valueOf(afShareGoodsDto.getRid()));
					afFreshmanGoodsVo.setDecreasePrice(afShareGoodsDto.getDecreasePrice());
					afFreshmanGoodsVo.setSaleAmount(afShareGoodsDto.getPriceAmount().toString());
					afFreshmanGoodsVo.setRealAmount(afShareGoodsDto.getSaleAmount().toString());
					afFreshmanGoodsVo.setRebateAmount(afShareGoodsDto.getRebateAmount().toString());
					afFreshmanGoodsVo.setGoodsName(afShareGoodsDto.getName());
					afFreshmanGoodsVo.setGoodsIcon(afShareGoodsDto.getGoodsIcon());
					afFreshmanGoodsVo.setThumbnailIcon(afShareGoodsDto.getThumbnailIcon());
					afFreshmanGoodsVo.setGoodsUrl(afShareGoodsDto.getGoodsDetail().split(";")[0]);
					afFreshmanGoodsVo.setOpenId(afShareGoodsDto.getOpenId());
					afFreshmanGoodsVo.setSource(afShareGoodsDto.getSource());
					resultList.add(afFreshmanGoodsVo);
				}
			}
			
			AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	        JSONArray array = JSON.parseArray(resource.getValue());
	        
	        //删除2分期
	        if (array == null) {
	            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
	        }
	        //removeSecondNper(array);
			
			for (AfFreshmanGoodsVo goodsInfo : resultList) {
                List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, null, BigDecimal.ONE.intValue(),
                         new BigDecimal(goodsInfo.getSaleAmount()), resource.getValue1(), resource.getValue2(),goodsInfo.getGoodsId(),"0");
                if (nperList != null) {
                    Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                    goodsInfo.setNperMap(nperMap);
                }
            }
			
			
			logger.info(JSON.toJSONString(resultList));
			data.put("goodsList", resultList);
			result = H5CommonResponse.getNewInstance(true, "获取商品列表成功", null, data).toString();
			
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("/activity/freshmanShareH5/homePage error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取商品列表失败", null, "").toString();
		}
		
		return result;
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
	
	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}
}
