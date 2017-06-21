package com.ald.fanbei.api.web.apph5.controller;
 
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;
 
/**
 * @类描述：
 * 返场活动
 * @author 江荣波 2017年6月20日下午1:41:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web")
@SuppressWarnings("unchecked")
public class AppH5EncoreController extends BaseController {
    String  opennative = "/fanbei-web/opennative?name=";
    
    @RequestMapping(value = "encoreActivityInfo", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String encoreActivityInfo(HttpServletRequest request, ModelMap model) throws IOException {
    	// 返场活动H5接口
    	JSONObject jsonObj = new JSONObject();
    	jsonObj.put("bannerUrl", "http://www.baidu.com");
    	jsonObj.put("currentTime", System.currentTimeMillis());
    	jsonObj.put("validStartTime", System.currentTimeMillis() -3600 * 1000l);
    	jsonObj.put("validEndTime", System.currentTimeMillis() +3600 * 1000l);
    	
    	List activityGoodsList  = new ArrayList();
    	{
    		Map map = new HashMap();
    		map.put("goodsName", "蕾丝打底衫女长袖2017春装新款女装雪纺上衣服显瘦蕾丝小衫秋冬潮");
    		map.put("specialAmount", "30.21");
    		map.put("rebateAmount", "3.60");
    		map.put("saleAmount", "50.12");
    		map.put("repertoryCount", "30");
    		map.put("startTime", System.currentTimeMillis());
    		map.put("validStartTime", System.currentTimeMillis() - 3600 * 1000l);
    		map.put("validEndTime", System.currentTimeMillis() + 3600 * 1000l);
    		map.put("goodsType", "0");
    		map.put("goodsIcon", "http://img04.taobaocdn.com/bao/uploaded/i4/TB1W0UbQFXXXXauXXXXXXXXXXXX_!!0-item_pic.jpg");
    		map.put("goodsId", "101706");
    		map.put("thumbnailIcon", "http://img02.taobaocdn.com/bao/uploaded/i2/TB1fLZ8NVXXXXXeaXXXXXXXXXXX_!!0-item_pic.jpg");
    		map.put("goodsUrl", "http://item.taobao.com/item.htm?id=540681251293");
    		activityGoodsList.add(map);
    	}
    	jsonObj.put("activityGoodsList", activityGoodsList);
    	
    	List recommendGoodsList = new ArrayList();
    	{
    		Map map = new HashMap();
    		map.put("goodsName", "蕾丝打底衫女长袖2017春装新款女装雪纺上衣服显瘦蕾丝小衫秋冬潮");
    		map.put("rebateAmount", "3.60");
    		map.put("saleAmount", "50.12");
    		map.put("goodsType", "0");
    		map.put("goodsIcon", "http://img04.taobaocdn.com/bao/uploaded/i4/TB1W0UbQFXXXXauXXXXXXXXXXXX_!!0-item_pic.jpg");
    		map.put("goodsId", "101706");
    		map.put("thumbnailIcon", "http://img02.taobaocdn.com/bao/uploaded/i2/TB1fLZ8NVXXXXXeaXXXXXXXXXXX_!!0-item_pic.jpg");
    		map.put("goodsUrl", "http://item.taobao.com/item.htm?id=540681251293");
    		recommendGoodsList.add(map);
    	}
    	jsonObj.put("recommendGoodsList", recommendGoodsList);
    	
    	return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",jsonObj).toString(); 
    }
    
    
    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }
    
    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }
 
    @Override
    public String doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
 
}