package com.ald.fanbei.api.web.api.user;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserWithholdService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述:
 * @author fanmanfu 创建时间：2017年11月6日 下午1:20:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/Withhold")
public class WithholdController extends BaseController {

	@Resource
	AfUserWithholdService afUserWithholdService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserBankcardService afUserBankCardService;

	@RequestMapping(value = "/updateWithholdSwitch", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String updateWithholdSwitch(HttpServletRequest request) { // 代扣开关
		String userName = ObjectUtils.toString(request.getParameter("userName"));
		String IsSwitch = ObjectUtils.toString(request.getParameter("isSwitch"));
		
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		AfUserWithholdDo afUserWithholdDo = afUserWithholdService.getAfUserWithholdDtoByUserId(userDo.getRid());
		if (afUserWithholdDo == null) {
				AfUserBankcardDo mainBankcard = afUserBankCardService.getUserMainBankcardByUserId(userDo.getRid());
				
				AfUserWithholdDo userWithholdDo = new AfUserWithholdDo();
				try {
					userWithholdDo.setUserName(userDo.getUserName());
					userWithholdDo.setUserId(userDo.getRid());
					userWithholdDo.setCardNumber1(mainBankcard.getCardNumber());
					userWithholdDo.setCardId1(mainBankcard.getRid());
					userWithholdDo.setIsWithhold(Integer.parseInt(IsSwitch));
					afUserWithholdService.insertAfUserWithholdDto(userWithholdDo);
				} catch (Exception e){
					logger.info("insertAfUserWithholdDto is fail Exception is :"+e);
				}
		} else {
			afUserWithholdService.updateAfUserWithholdDtoByUserId(userDo.getRid(), Integer.parseInt(IsSwitch));
		}
		return null;
	}

	@RequestMapping(value = "/updateWithholdCard", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String updateWithholdCard(HttpServletRequest request) {             // 更新扣款顺序

		String userName = ObjectUtils.toString(request.getParameter("userName"));
		String card1 = ObjectUtils.toString(request.getParameter("card1"));
		String card2 = ObjectUtils.toString(request.getParameter("card2"));
		String card3 = ObjectUtils.toString(request.getParameter("card3"));
		String card4 = ObjectUtils.toString(request.getParameter("card4"));
		String card5 = ObjectUtils.toString(request.getParameter("card5"));
		String usebalance = ObjectUtils.toString(request.getParameter("usebalance"));
		
		logger.info("userName="+userName+"; card1="+card1+"; card2="+card2+"; card3="+card3+"; card4="+card4+"; card5="+card5+"; usebalance="+usebalance); 
		
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		if(userDo == null){
			logger.info("userDo is not exist ,userName=:"+userName);
		}
		AfUserWithholdDo afUserWithholdDo = new AfUserWithholdDo();

		// 得到银行卡ID
		AfUserBankcardDo cardId1 = null;
		AfUserBankcardDo cardId2 = null;
		AfUserBankcardDo cardId3 = null;
		AfUserBankcardDo cardId4 = null;
		AfUserBankcardDo cardId5 = null;
		if (StringUtil.isNotBlank(card1)) {
			cardId1 = afUserBankCardService.getUserBankcardIdByCardNumber(card1);
			afUserWithholdDo.setCardNumber1(card1);
			afUserWithholdDo.setCardId1(cardId1.getRid());
		}
		if (StringUtil.isNotBlank(card2)) {
			cardId2 = afUserBankCardService.getUserBankcardIdByCardNumber(card2);
			afUserWithholdDo.setCardNumber2(card2);
			afUserWithholdDo.setCardId2(cardId2.getRid());
		}
		if (StringUtil.isNotBlank(card3)) {
			cardId3 = afUserBankCardService.getUserBankcardIdByCardNumber(card3);
			afUserWithholdDo.setCardNumber3(card3);
			afUserWithholdDo.setCardId3(cardId3.getRid());
		}
		if (StringUtil.isNotBlank(card4)) {
			cardId4 = afUserBankCardService.getUserBankcardIdByCardNumber(card4);
			afUserWithholdDo.setCardNumber4(card4);
			afUserWithholdDo.setCardId4(cardId4.getRid());
		}
		if (StringUtil.isNotBlank(card5)) {
			cardId5 = afUserBankCardService.getUserBankcardIdByCardNumber(card5);
			afUserWithholdDo.setCardNumber5(card5);
			afUserWithholdDo.setCardId5(cardId5.getRid());
		}

		//AfUserWithholdDo afUserWithholdDo1 = afUserWithholdService.getAfUserWithholdDtoByUserId(userDo.getRid());
		afUserWithholdDo.setUserId(userDo.getRid());
		if(usebalance != null && usebalance != "") {
			afUserWithholdDo.setUsebalance(Integer.parseInt(usebalance));
		}

		afUserWithholdService.updateAfUserWithholdDo(afUserWithholdDo);
		return null;

	}

	@RequestMapping(value = "/getWithholdInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8" )
	@ResponseBody
	public String getWithholdInfo(HttpServletRequest request, Model model) {                  // 得到代扣初始信息
		String appInfo = getAppInfo(request.getHeader("Referer"));
		String userName = StringUtil.null2Str(JSON.parseObject(appInfo).get("userName"));
		
		if(userName == null || userName == "") {
			logger.info("userName is null");
			return "userName is null";
		}
		
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		Map<String,Object> info = new HashMap<String,Object>();
		if (userDo != null) {
			AfUserWithholdDo withholdInfo = afUserWithholdService.getWithholdInfo(userDo.getRid());
			if(withholdInfo != null) {
				if (withholdInfo.getIsWithhold().equals("0")){
					info.put("IsWithhold", "0");
				} else {
					info.put("IsWithhold", "1");
					info.put("usebalance", withholdInfo.getUsebalance());
				}
			} else {
				info.put("IsWithhold", "0");
			}
			model.addAttribute("withholdInfo", info);
		}
		return JsonUtil.toJSONString(info);
	}

	@RequestMapping(value = "/showUserBankCard", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String showUserBankCard(HttpServletRequest request, Model model) { // 展示所有银行卡
		String userName = ObjectUtils.toString(request.getParameter("userName"));
		
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		Map<String, String> bankInfo = new HashMap<String, String>();
		List<String>  bankInfos = new ArrayList<String>();
		if (userDo != null) {
			AfUserWithholdDo withhold = afUserWithholdService.getWithholdInfo(userDo.getRid());

			List<AfUserBankcardDo> afUserBankcardDoList = afUserBankCardService.getAfUserBankcardDoList(userDo.getRid());//得到所有的银行卡
			if (afUserBankcardDoList != null && afUserBankcardDoList.size() > 0) {
				for (AfUserBankcardDo afUserBankcardDo : afUserBankcardDoList) {
					if (afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber1())) {
						String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
						bankInfo.put("card1", bank);
					} else if (afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber2())) {
						String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
						bankInfo.put("card2", bank);
					} else if (afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber3())) {
						String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
						bankInfo.put("card3", bank);
					} else if (afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber4())) {
						String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
						bankInfo.put("card4", bank);
					} else if (afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber5())) {
						String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
						bankInfo.put("card5", bank);
					} else {
						bankInfos.add(afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")");
					}
				}
				model.addAttribute("card", bankInfo);//代扣已绑定银行卡
				model.addAttribute("cards", bankInfos);//代扣未绑定银行卡
			
			} else {
				logger.info("This is not bankCard userId=:"+userDo.getRid());
			}
		}
		String jsonString1 = JsonUtil.toJSONString(bankInfo);
		String jsonString2 = JsonUtil.toJSONString(bankInfos);
		return jsonString1+jsonString2;
	}

	
	private static String getAppInfo(String url) {
		if (StringUtil.isBlank(url)) {
			return null;
		}
		String result = "";
		try {
			Map<String, List<String>> params = new HashMap<String, List<String>>();
			String[] urlParts = url.split("\\?");
			if (urlParts.length > 1) {
				String query = urlParts[1];
				for (String param : query.split("&")) {
					String[] pair = param.split("=");
					String key = URLDecoder.decode(pair[0], "UTF-8");
					String value = "";
					if (pair.length > 1) {
						value = URLDecoder.decode(pair[1], "UTF-8");
					}

					List<String> values = params.get(key);
					if (values == null) {
						values = new ArrayList<String>();
						params.put(key, values);
					}
					values.add(value);
				}
			}
			List<String> _appInfo = params.get("_appInfo");
			if (_appInfo != null && _appInfo.size() > 0) {
				result = _appInfo.get(0);
			}
			return result;
		} catch (UnsupportedEncodingException ex) {
			throw new AssertionError(ex);
		}
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
