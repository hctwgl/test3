package com.ald.fanbei.api.web.third.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.biz.third.util.YoudunUtil;

/**
 * 
 *@类描述：第三方回调接口
 *@author 陈金虎 2017年1月17日 下午6:14:52
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/zm")
public class ZmxyController{
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private YoudunUtil youdunUtil;
	
	/**
	 * 支付宝回调接口
	 * @param request
	 * @param response
	 * @return
	 */
    @RequestMapping(value = {"/authNotify"}, method = RequestMethod.GET)
	public String alipayNotify(HttpServletRequest request, HttpServletResponse response){
    	Map<String, String[]> params = request.getParameterMap();
    	for(String item:params.keySet()){
    		if(params.get(item) != null){
    			System.out.println("item=" + item + ",value=" + params.get(item)[0]);
    			logger.info("item=" + item + ",value=" + params.get(item)[0]);
    		}
    	}
//    	AfYoudunFaceDo youdunDo = new AfYoudunFaceDo();
//    	youdunDo.setAddress(request.getParameter("address"));
//    	youdunDo.setAge(NumberUtil.objToIntDefault(request.getParameter("age"), 0));
//    	youdunDo.setBackCard(request.getParameter("back_card"));
//    	youdunDo.setBeIdcard(NumberUtil.objToBigDecimalDefault(request.getParameter("be_idcard"), new BigDecimal("0.0000")));
//    	youdunDo.setBirthday(request.getParameter("birthday"));
//    	youdunDo.setFailReason(request.getParameter("fail_reason"));
//    	youdunDo.setFrontCard(request.getParameter("front_card"));
//    	youdunDo.setGender(request.getParameter("gender"));
//    	youdunDo.setIdName(request.getParameter("id_name"));
//    	youdunDo.setIdNo(request.getParameter("id_no"));
//    	youdunDo.setInfoOrder(request.getParameter("info_order"));
//    	youdunDo.setIssuingAuthority(request.getParameter("issuing_authority"));
//    	youdunDo.setNation(request.getParameter("nation"));
//    	youdunDo.setNoProduct(request.getParameter("no_product"));
//    	youdunDo.setOidPartner(request.getParameter("oid_partner"));
//    	youdunDo.setNoOrder(request.getParameter("no_order"));
//    	youdunDo.setPhotoGet(request.getParameter("photo_get"));
//    	youdunDo.setPhotoGrid(request.getParameter("photo_grid"));
//    	youdunDo.setPhotoLiving(request.getParameter("photo_living"));
//    	youdunDo.setResultAuth(request.getParameter("result_auth"));
//    	youdunDo.setSign(request.getParameter("sign"));
//    	youdunDo.setSignField(request.getParameter("sign_field"));
//    	youdunDo.setUserId(request.getParameter("user_id"));
//    	youdunDo.setValidityPeriod(request.getParameter("validity_period"));
//    	
//    	youdunUtil.dealYoudunNotify(youdunDo);
    	
    	//TODO 更新af_user_auth表对应的有盾信息
    	
    	return "/common/authSucc";
	}
	
}