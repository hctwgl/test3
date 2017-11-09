package com.ald.fanbei.api.web.api.user;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserWithholdService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;



/** 
 * @类描述:
 * @author fanmanfu 创建时间：2017年11月6日 下午1:20:42 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/Withhold")
public class WithholdController extends BaseController{

	
	@Resource
	AfUserWithholdService afUserWithholdService;
	@Resource
	AfUserService afUserService;
	
	@RequestMapping("/updateWithholdSwitch")
	@ResponseBody
	public String updateWithholdSwitch(HttpServletRequest request){
		String userName = ObjectUtils.toString(request.getParameter("userName"));
		Integer IsSwitch =Integer.parseInt((request.getParameter("IsSwitch")).toString());
		
		AfUserWithholdDo afUserWithholdDo = afUserWithholdService.getAfUserWithholdDtoByUserId(userName);
		if(afUserWithholdDo == null) {
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			if(userDo != null) {
				AfUserWithholdDo userWithholdDo = new AfUserWithholdDo();
				userWithholdDo.setUserName(userDo.getUserName());
				userWithholdDo.setUserId(userDo.getRid());
				userWithholdDo.setIsWithhold(IsSwitch);
				userWithholdDo.setUsebalance(1);
				afUserWithholdService.insertAfUserWithholdDto(userWithholdDo);
			} else {
				logger.info("updateWithholdSwitch :user is not exist");
			}
		}else {
			afUserWithholdService.updateAfUserWithholdDtoByUserName(userName, IsSwitch);
		}
		return null;
	}
	
	@RequestMapping("/updateWithholdCard")
	@ResponseBody
	public String updateWithholdCard(HttpServletRequest request){
		
		String userName = ObjectUtils.toString(request.getParameter("userName"));
		String card1 = ObjectUtils.toString(request.getParameter("card1"));
		String card2 = ObjectUtils.toString(request.getParameter("card2"));
		String card3 = ObjectUtils.toString(request.getParameter("card3"));
		String card4 = ObjectUtils.toString(request.getParameter("card4"));
		String card5 = ObjectUtils.toString(request.getParameter("card5"));
		Integer usebalance = Integer.parseInt((request.getParameter("usebalance")).toString());
		
		AfUserWithholdDo afUserWithholdDo1 = afUserWithholdService.getAfUserWithholdDtoByUserId(userName);
		AfUserWithholdDo afUserWithholdDo2 = new AfUserWithholdDo();
		afUserWithholdDo2.setCardNumber1(card1);
		afUserWithholdDo2.setCardNumber2(card2);
		afUserWithholdDo2.setCardNumber3(card3);
		afUserWithholdDo2.setCardNumber4(card4);
		afUserWithholdDo2.setCardNumber5(card5);
		afUserWithholdDo2.setUserId(afUserWithholdDo1.getUserId());
		afUserWithholdDo2.setUsebalance(usebalance);
		
		afUserWithholdService.updateAfUserWithholdDo(afUserWithholdDo2);
		return null;
		
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}
}
