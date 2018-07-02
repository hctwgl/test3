package com.ald.fanbei.api.web.api.withhold;

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
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述:
 * @author fanmanfu 创建时间：2017年11月6日 下午1:20:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller("updateWithholdCardApi")
public class WithholdUpdateCardController implements ApiHandle {

	@Resource
	AfUserWithholdService afUserWithholdService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserBankcardService afUserBankCardService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String userName = context.getUserName();
		String usebalance = ObjectUtils.toString(requestDataVo.getParams().get("usebalance"));

		String card1 = ObjectUtils.toString(requestDataVo.getParams().get("card1"));
		String card2 = ObjectUtils.toString(requestDataVo.getParams().get("card2"));
		String card3 = ObjectUtils.toString(requestDataVo.getParams().get("card3"));
		String card4 = ObjectUtils.toString(requestDataVo.getParams().get("card4"));
		String card5 = ObjectUtils.toString(requestDataVo.getParams().get("card5"));

		logger.info("userName=" + userName + "; card1=" + card1 + "; card2=" + card2 + "; card3=" + card3 + "; card4=" + card4 + "; card5=" + card5 + "; usebalance=" + usebalance);

		AfUserDo userDo = afUserService.getUserByUserName(userName);
		if (userDo == null) {
			logger.info("userDo is not exist ,userName=:" + userName);
		}
		AfUserWithholdDo afUserWithholdDo = new AfUserWithholdDo();

		// 得到银行卡ID
		AfUserBankcardDo cardId1 = null;
		AfUserBankcardDo cardId2 = null;
		AfUserBankcardDo cardId3 = null;
		AfUserBankcardDo cardId4 = null;
		AfUserBankcardDo cardId5 = null;
		if (usebalance != null && usebalance != "") {
			afUserWithholdDo.setUsebalance(Integer.parseInt(usebalance));
		} else {
			if (card1 != null && card1 != "") {
				cardId1 = afUserBankCardService.getUserBankcardIdByCardNumber(card1);
				afUserWithholdDo.setCardNumber1(card1);
				afUserWithholdDo.setCardId1(cardId1.getRid());
			} else {
				afUserWithholdDo.setCardNumber1("");
				afUserWithholdDo.setCardId1(0l);
			}
			if (card2 != null && card2 != "") {
				cardId2 = afUserBankCardService.getUserBankcardIdByCardNumber(card2);
				afUserWithholdDo.setCardNumber2(card2);
				afUserWithholdDo.setCardId2(cardId2.getRid());
			} else {
				afUserWithholdDo.setCardNumber2("");
				afUserWithholdDo.setCardId2(0l);
			}
			if (card3 != null && card3 != "") {
				cardId3 = afUserBankCardService.getUserBankcardIdByCardNumber(card3);
				afUserWithholdDo.setCardNumber3(card3);
				afUserWithholdDo.setCardId3(cardId3.getRid());
			} else {
				afUserWithholdDo.setCardNumber3("");
				afUserWithholdDo.setCardId3(0l);
			}
			if (StringUtil.isNotBlank(card4)) {
				cardId4 = afUserBankCardService.getUserBankcardIdByCardNumber(card4);
				afUserWithholdDo.setCardNumber4(card4);
				afUserWithholdDo.setCardId4(cardId4.getRid());
			} else {
				afUserWithholdDo.setCardNumber4("");
				afUserWithholdDo.setCardId4(0l);
			}
			if (StringUtil.isNotBlank(card5)) {
				cardId5 = afUserBankCardService.getUserBankcardIdByCardNumber(card5);
				afUserWithholdDo.setCardNumber5(card5);
				afUserWithholdDo.setCardId5(cardId5.getRid());
			} else {
				afUserWithholdDo.setCardNumber5("");
				afUserWithholdDo.setCardId5(0l);
			}
		}
		// AfUserWithholdDo afUserWithholdDo1 = afUserWithholdService.getAfUserWithholdDtoByUserId(userDo.getRid());
		afUserWithholdDo.setUserId(userDo.getRid());

		if (!(afUserWithholdService.updateAfUserWithholdDo(afUserWithholdDo) > 0)) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		Map<String, Integer> map = new HashMap<String, Integer>();
		AfUserWithholdDo withholdInfo = afUserWithholdService.getWithholdInfo(userDo.getRid());
		map.put("usebalance", withholdInfo.getUsebalance());
		resp.setResponseData(map);
		return resp;
	}

}
