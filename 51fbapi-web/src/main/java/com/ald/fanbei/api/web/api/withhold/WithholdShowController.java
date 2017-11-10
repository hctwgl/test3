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
@Controller("showUserBankCardApi")
public class WithholdShowController  implements ApiHandle {

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
	
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			Map<String, String> bankInfo1 = new HashMap<String, String>();
			Map<String, String> bankInfo2 = new HashMap<String, String>();
			Map<String, String> bankInfo3 = new HashMap<String, String>();
			Map<String, String> bankInfo4 = new HashMap<String, String>();
			Map<String, String> bankInfo5 = new HashMap<String, String>();
			
			//Map<String, Object> card = new HashMap<String, Object>();
			List<String>  bankInfos = new ArrayList<String>();
			List<Object> list = new ArrayList<Object>();
			List<Object> card = new ArrayList<Object>();
			if (userDo != null) {
				AfUserWithholdDo withhold = afUserWithholdService.getWithholdInfo(userDo.getRid());

				List<AfUserBankcardDo> afUserBankcardDoList = afUserBankCardService.getAfUserBankcardDoList(userDo.getRid());//得到所有的银行卡
				if (afUserBankcardDoList != null && afUserBankcardDoList.size() > 0) {
					for (AfUserBankcardDo afUserBankcardDo : afUserBankcardDoList) {
						if (withhold.getCardNumber1() != null && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber1())) {
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo1.put("card", bank);
							bankInfo1.put("sort", "1");
							list.add(bankInfo1);
						} else if (withhold.getCardNumber2() != null && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber2())) {
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo2.put("card", bank);
							bankInfo2.put("sort", "2");
							list.add(bankInfo2);
						} else if (withhold.getCardNumber3() != null && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber3())) {
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo3.put("card", bank);
							bankInfo3.put("sort", "3");
							list.add(bankInfo3);
						} else if (withhold.getCardNumber4() != null && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber4())) {
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo4.put("card", bank);
							bankInfo4.put("sort", "4");
							list.add(bankInfo4);
						} else if (withhold.getCardNumber5() != null && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber5())) {
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo5.put("card", bank);
							bankInfo5.put("sort", "5");
							list.add(bankInfo5);
						} else {
							bankInfos.add(afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")");
						}
					}
				} else {
					resp.setResponseData("N");
					logger.info("This is not bankCard userId=:"+userDo.getRid());
				}
			}
		
			
			card.add(list);
			card.add(bankInfos);
			resp.setResponseData(card);
			return resp;
	}
	
	
	

}
