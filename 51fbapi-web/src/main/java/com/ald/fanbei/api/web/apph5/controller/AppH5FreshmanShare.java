package com.ald.fanbei.api.web.apph5.controller;

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
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfShareGoodsDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.InterestFreeUitl;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfFreshmanGoodsVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**  
 * @Title: AppH5FreshmanShare.java
 * @Package com.ald.fanbei.api.web.apph5.controller
 * @Description: 新人专项活动
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年11月1日 下午1:31:31
 * @version V1.0  
 */
@RestController
@RequestMapping(value = "/activity/freshmanShare", produces = "application/json;charset=UTF-8")
public class AppH5FreshmanShare extends BaseController{
	
	
	@Resource
	AfUserService afUserService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfShareGoodsService afShareGoodsService;
	@Resource
	AfResourceService afResourceService;
	
	String opennative = "/fanbei-web/opennative?name=";
	
	/**
	 * 
	* @Title: share
	* @Description: 主页面
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/homePage", method = RequestMethod.POST)
	public String homePage(HttpServletRequest request, HttpServletResponse response) {
		String result = "";
		try {
			doWebCheck(request, false);
			
			List<AfShareGoodsDto> shareGoods = afShareGoodsService.getShareGoods();
			
			List<AfFreshmanGoodsVo> resultList = new ArrayList<AfFreshmanGoodsVo>();
			
			if(shareGoods!=null){
				
				for (AfShareGoodsDto afShareGoodsDto : shareGoods) {
					AfFreshmanGoodsVo afFreshmanGoodsVo = new AfFreshmanGoodsVo();
					afFreshmanGoodsVo.setNumId(afShareGoodsDto.getNumId());
					afFreshmanGoodsVo.setDecreasePrice(afShareGoodsDto.getDecreasePrice());
					afFreshmanGoodsVo.setSaleAmount(afShareGoodsDto.getPriceAmount().toString());
					afFreshmanGoodsVo.setRealAmount(afShareGoodsDto.getSaleAmount().toString());
					afFreshmanGoodsVo.setRebateAmount(afShareGoodsDto.getRebateAmount().toString());
					afFreshmanGoodsVo.setGoodsName(afShareGoodsDto.getName());
					afFreshmanGoodsVo.setGoodsIcon(afShareGoodsDto.getGoodsIcon());
					afFreshmanGoodsVo.setThumbnailIcon(afShareGoodsDto.getThumbnailIcon());
					afFreshmanGoodsVo.setGoodsUrl(afShareGoodsDto.getGoodsDetail().split(";")[0]);
					afFreshmanGoodsVo.setOpenId(afShareGoodsDto.getOpenId());
					resultList.add(afFreshmanGoodsVo);
				}
			}
			
			AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CONSUME);
	        JSONArray array = JSON.parseArray(resource.getValue());
	        
	        //删除2分期
	        if (array == null) {
	            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
	        }
	        removeSecondNper(array);
			
			for (AfFreshmanGoodsVo goodsInfo : resultList) {
                List<Map<String, Object>> nperList = InterestFreeUitl.getConsumeList(array, null, BigDecimal.ONE.intValue(),
                         new BigDecimal(goodsInfo.getSaleAmount()), resource.getValue1(), resource.getValue2());
                if (nperList != null) {
                    Map<String, Object> nperMap = nperList.get(nperList.size() - 1);
                    goodsInfo.setNperMap(nperMap);
                }
            }
			
			
			logger.info(JSON.toJSONString(resultList));
			result = H5CommonResponse.getNewInstance(true, "获取商品列表成功", null, JSON.toJSONString(resultList)).toString();
			
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("/activity/freshmanShareH5/homePage error = {}", e.getStackTrace());
			return H5CommonResponse.getNewInstance(false, "获取商品列表失败", null, "").toString();
		}
		
		return result;
	}
	
	
	/**
	 * 
	* @Title: isNew
	* @Description: 验证是否是新用户
	* @param request
	* @param response
	* @return    
	* @return String   
	* @throws
	 */
	@RequestMapping(value = "/isNew", method = RequestMethod.POST)
	public String isNew(HttpServletRequest request, HttpServletResponse response) {
		String resultStr = "";
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		
		try {
				context = doWebCheck(request, false);
				String userName = context.getUserName();
				Long userId = convertUserNameToUserId(userName);
				if (null != userId){
				/*//根据用户名查询用户信息
				AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
				logger.info(JSON.toJSONString(userInfo));*/
				//查询用户订单数
				int oldUserOrderAmount = afOrderService.getOldUserOrderAmount(userId);
				if(oldUserOrderAmount==0){
					data.put("isNew", "Y");//新用户
				}else{
					data.put("isNew", "N");//老用户
				}
				logger.info(JSON.toJSONString(data));
				
				resultStr = H5CommonResponse.getNewInstance(true, "验证是否是新用户成功", null, data).toString();
				return resultStr;
			}else{
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
			logger.error("/activity/freshmanShare/isNew" + context + "login error ");
			resultStr = H5CommonResponse.getNewInstance(false, "没有登录", null, data).toString();
			}
		} catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
					|| e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
			logger.error("/activity/freshmanShare/isNew" + context + "login error ");
			resultStr = H5CommonResponse.getNewInstance(false, "没有登录", null, data).toString();
			}
		}catch (Exception e) {
			logger.error("/activity/freshmanShare/isNew" + context + "error = {}", e.getStackTrace());
			resultStr = H5CommonResponse.getNewInstance(false, "验证是否是新用户失败").toString();
			return resultStr;
		}
		
		return resultStr;
	}

	
	
	
	private void removeSecondNper(JSONArray array) {
        if (array == null) {
            return;
        }
        Iterator<Object> it = array.iterator();
        while (it.hasNext()) {
            JSONObject json = (JSONObject) it.next();
            if (json.getString(Constants.DEFAULT_NPER).equals("2")) {
                it.remove();
                break;
            }
        }
    }
	

	private Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}

		}
		return userId;
	}


	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}
	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
