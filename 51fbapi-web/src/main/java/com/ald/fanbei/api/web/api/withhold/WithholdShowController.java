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
			
			
			List<Object> list1 = new ArrayList<Object>();
			
			Map<String, Object> cards = new HashMap<String, Object>();
			
			if (userDo != null) {
				AfUserWithholdDo withhold = afUserWithholdService.getWithholdInfo(userDo.getRid());
				
				List<AfUserBankcardDo> afUserBankcardDoList = afUserBankCardService.getAfUserBankcardDoList(userDo.getRid());//得到所有的银行卡
			try{
				if (afUserBankcardDoList != null && afUserBankcardDoList.size() > 0) {
					
					for (AfUserBankcardDo afUserBankcardDo : afUserBankcardDoList) {
						/*if(afUserBankcardDo.getRid().equals(withholdInfo.getCardId1())) {
							if(afUserBankcardDo.getRid().equals(withholdInfo.getCardId2())) {
								if(afUserBankcardDo.getRid().equals(withholdInfo.getCardId3())) {
									if(afUserBankcardDo.getRid().equals(withholdInfo.getCardId4())) {
										if(afUserBankcardDo.getRid().equals(withholdInfo.getCardId5())) {
										  
										} else{}
									}
								}
							}
							}
							Map<String, String> bankInfo = new HashMap<String, String>();
							if (afUserBankcardDo.getIsMain().equals("Y")) {
								bankInfo.put("isMain", "Y");
							} else {
								bankInfo.put("isMain", "N");
							}
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo.put("card", bank);
							bankInfo.put("sort", "1");
							bankInfo.put("cardId",afUserBankcardDo.getCardNumber()+"");
							list1.add(bankInfo);
						}*/
						
						if (withhold.getCardNumber1() != null && withhold.getCardNumber1() != "" && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber1())) {
							if (afUserBankcardDo.getIsMain().equals("Y")) {
								bankInfo1.put("isMain", "Y");
							} else {
								bankInfo1.put("isMain", "N");
							}
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo1.put("card", bank);
							bankInfo1.put("sort", "1");
							bankInfo1.put("cardId",afUserBankcardDo.getCardNumber()+"");
							list1.add(bankInfo1);
						} else if (withhold.getCardNumber2() != null && withhold.getCardNumber2() != "" && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber2())) {
							if (afUserBankcardDo.getIsMain().equals("Y")) {
								bankInfo2.put("isMain", "Y");
							} else {
								bankInfo2.put("isMain", "N");
							}
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo2.put("card", bank);
							bankInfo2.put("sort", "2");
							bankInfo2.put("cardId",afUserBankcardDo.getCardNumber()+"");
							list1.add(bankInfo2);
						} else if (withhold.getCardNumber3() != null && withhold.getCardNumber3() != "" && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber3())) {
							if (afUserBankcardDo.getIsMain().equals("Y")) {
								bankInfo3.put("isMain", "Y");
							} else {
								bankInfo3.put("isMain", "N");
							}
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo3.put("card", bank);
							bankInfo3.put("sort", "3");
							bankInfo3.put("cardId",afUserBankcardDo.getCardNumber()+"");
							list1.add(bankInfo3);
						} else if (withhold.getCardNumber4() != null && withhold.getCardNumber4() != "" && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber4())) {
							if (afUserBankcardDo.getIsMain().equals("Y")) {
								bankInfo4.put("isMain", "Y");
							} else {
								bankInfo4.put("isMain", "N");
							}
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo4.put("card", bank);
							bankInfo4.put("sort", "4");
							bankInfo4.put("cardId",afUserBankcardDo.getCardNumber()+"");
							list1.add(bankInfo4);
						} else if (withhold.getCardNumber5() != null && withhold.getCardNumber5() != "" && afUserBankcardDo.getCardNumber().equals(withhold.getCardNumber5())) {
							if (afUserBankcardDo.getIsMain().equals("Y")) {
								bankInfo5.put("isMain", "Y");
							} else {
								bankInfo5.put("isMain", "N");
							}
							String bank = afUserBankcardDo.getBankName() + "(" + afUserBankcardDo.getCardNumber().substring(afUserBankcardDo.getCardNumber().length() - 4) + ")";
							bankInfo5.put("card", bank);
							bankInfo5.put("sort", "5");
							bankInfo5.put("cardId",afUserBankcardDo.getCardNumber()+"");
							list1.add(bankInfo5);
						} else {
							List<Object> list2 = new ArrayList<Object>();
							int i=1;
							for (AfUserBankcardDo afUserBankcardDos : afUserBankcardDoList) {
								Map<String, String> bankInfos = new HashMap<String, String>();
								
								bankInfos.put("card",(afUserBankcardDos.getBankName() + "(" + afUserBankcardDos.getCardNumber().substring(afUserBankcardDos.getCardNumber().length() - 4) + ")"));
								bankInfos.put("sort", i+"");
								bankInfos.put("cardId",afUserBankcardDos.getCardNumber()+"");
								bankInfos.put("isMain", afUserBankcardDos.getIsMain());
								list2.add(bankInfos);
								i++;
							}
							cards.put("card1", list2);
							resp.setResponseData(cards);
							return resp;
						}
					}
				} 
			} catch (Exception e) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR); 
			}
			}
		
			cards.put("card1", list1);
			resp.setResponseData(cards);
			return resp;
	}
	
	
	

}
