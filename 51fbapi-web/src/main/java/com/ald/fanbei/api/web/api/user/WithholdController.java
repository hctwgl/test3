package com.ald.fanbei.api.web.api.user;

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
		Integer IsSwitch = Integer.parseInt((request.getParameter("IsSwitch")).toString());

		AfUserDo userDo = afUserService.getUserByUserName(userName);
		AfUserWithholdDo afUserWithholdDo = afUserWithholdService.getAfUserWithholdDtoByUserId(userDo.getRid());
		if (afUserWithholdDo == null) {
				AfUserBankcardDo mainBankcard = afUserBankCardService.getUserMainBankcardByUserId(userDo.getRid());
				AfUserWithholdDo userWithholdDo = new AfUserWithholdDo();
				userWithholdDo.setUserName(userDo.getUserName());
				userWithholdDo.setUserId(userDo.getRid());
				userWithholdDo.setCardNumber1(mainBankcard.getCardNumber());
				userWithholdDo.setCardId1(mainBankcard.getRid());
				userWithholdDo.setIsWithhold(IsSwitch);
				afUserWithholdService.insertAfUserWithholdDto(userWithholdDo);
		} else {
			afUserWithholdService.updateAfUserWithholdDtoByUserName(userName, IsSwitch);
		}
		return null;
	}

	@RequestMapping(value = "/updateWithholdCard", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String updateWithholdCard(HttpServletRequest request) { // 更新扣款顺序

		String userName = ObjectUtils.toString(request.getParameter("userName"));
		String card1 = ObjectUtils.toString(request.getParameter("card1"));
		String card2 = ObjectUtils.toString(request.getParameter("card2"));
		String card3 = ObjectUtils.toString(request.getParameter("card3"));
		String card4 = ObjectUtils.toString(request.getParameter("card4"));
		String card5 = ObjectUtils.toString(request.getParameter("card5"));
		Integer usebalance = Integer.parseInt((request.getParameter("usebalance")).toString());
		
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		// 得到银行卡ID
		AfUserBankcardDo cardId1 = null;
		AfUserBankcardDo cardId2 = null;
		AfUserBankcardDo cardId3 = null;
		AfUserBankcardDo cardId4 = null;
		AfUserBankcardDo cardId5 = null;
		if (StringUtil.isNotBlank(card1)) {
			cardId1 = afUserBankCardService.getUserBankcardIdByCardNumber(card1);
		}
		if (StringUtil.isNotBlank(card2)) {
			cardId2 = afUserBankCardService.getUserBankcardIdByCardNumber(card2);
		}
		if (StringUtil.isNotBlank(card3)) {
			cardId3 = afUserBankCardService.getUserBankcardIdByCardNumber(card3);
		}
		if (StringUtil.isNotBlank(card4)) {
			cardId4 = afUserBankCardService.getUserBankcardIdByCardNumber(card4);
		}
		if (StringUtil.isNotBlank(card5)) {
			cardId5 = afUserBankCardService.getUserBankcardIdByCardNumber(card5);
		}

		AfUserWithholdDo afUserWithholdDo1 = afUserWithholdService.getAfUserWithholdDtoByUserId(userDo.getRid());
		AfUserWithholdDo afUserWithholdDo2 = new AfUserWithholdDo();
		afUserWithholdDo2.setCardNumber1(card1);
		afUserWithholdDo2.setCardId1(cardId1.getRid());
		afUserWithholdDo2.setCardNumber2(card2);
		afUserWithholdDo2.setCardId2(cardId2.getRid());
		afUserWithholdDo2.setCardNumber3(card3);
		afUserWithholdDo2.setCardId3(cardId3.getRid());
		afUserWithholdDo2.setCardNumber4(card4);
		afUserWithholdDo2.setCardId4(cardId4.getRid());
		afUserWithholdDo2.setCardNumber5(card5);
		afUserWithholdDo2.setCardId5(cardId5.getRid());
		afUserWithholdDo2.setUserId(afUserWithholdDo1.getUserId());
		afUserWithholdDo2.setUsebalance(usebalance);

		afUserWithholdService.updateAfUserWithholdDo(afUserWithholdDo2);
		return null;

	}

	@RequestMapping(value = "/getWithholdInfo", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getWithholdInfo(HttpServletRequest request, Model model) { // 得到代扣初始信息
		String userName = ObjectUtils.toString(request.getParameter("userName"));
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		if (userDo != null) {
			AfUserWithholdDo withholdInfo = afUserWithholdService.getWithholdInfo(userDo.getRid());
			model.addAttribute("withholdInfo", withholdInfo);
		}
		return null;
	}

	@RequestMapping(value = "/showUserBankCard", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String showUserBankCard(HttpServletRequest request, Model model) { // 展示所有银行卡
		String userName = ObjectUtils.toString(request.getParameter("userName"));
		
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		if (userDo != null) {
			AfUserWithholdDo withhold = afUserWithholdService.getWithholdInfo(userDo.getRid());

			List<AfUserBankcardDo> afUserBankcardDoList = afUserBankCardService.getAfUserBankcardDoList(userDo.getRid());//得到所有的银行卡
			if (afUserBankcardDoList != null && afUserBankcardDoList.size() > 0) {
				Map<String, String> bankInfo = new HashMap<String, String>();
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
						int i = 1;
						model.addAttribute("card" + i++, afUserBankcardDo);
					}
				}
				model.addAttribute("card", bankInfo);
			} else {
				logger.info("This is not bankCard userId=:"+userDo.getRid());
			}
		}
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
