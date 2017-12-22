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
@Controller("updateWithholdSwitchApi")
public class WithholdUpdateSwitchController  implements ApiHandle {

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
		String IsSwitch = ObjectUtils.toString(requestDataVo.getParams().get("isSwitch"));
		
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		Map<String,Integer> map = new HashMap<String,Integer>();
		AfUserWithholdDo afUserWithholdDo = afUserWithholdService.getAfUserWithholdDtoByUserId(userDo.getRid());
		if (afUserWithholdDo == null) {
				//AfUserBankcardDo mainBankcard = afUserBankCardService.getUserMainBankcardByUserId(userDo.getRid());
				AfUserWithholdDo userWithholdDo = new AfUserWithholdDo();
				String str = afUserBankCardService.getAfUserBankcardList(userDo.getRid());// 得到所有的银行卡字符串
				
				String[] card = str.split("\\,");
				try {
					userWithholdDo.setUserName(userDo.getUserName());
					userWithholdDo.setUserId(userDo.getRid());
					userWithholdDo.setIsWithhold(Integer.parseInt(IsSwitch));
					userWithholdDo.setUsebalance(1);
					if(card.length >= 1) {
						String[] split1 = card[0].split("\\|");
						userWithholdDo.setCardNumber1(split1[1]);
						userWithholdDo.setCardId1(Long.parseLong(split1[0]));
					}if(card.length >= 2) {
						String[] split2 = card[1].split("\\|");
						userWithholdDo.setCardNumber2(split2[1]);
						userWithholdDo.setCardId2(Long.parseLong(split2[0]));
					}  if(card.length >= 3) {
						String[] split3 = card[2].split("\\|");
						userWithholdDo.setCardNumber3(split3[1].toString());
						userWithholdDo.setCardId3(Long.parseLong(split3[0].toString()));
					}  if(card.length >= 4) {
						String[] split4 = card[3].split("\\|");
						userWithholdDo.setCardNumber4(split4[1]);
						userWithholdDo.setCardId4(Long.parseLong(split4[0]));
					} if(card.length >= 5) {
						String[] split5 = card[4].split("\\|");
						userWithholdDo.setCardNumber5(split5[1]);
						userWithholdDo.setCardId5(Long.parseLong(split5[0]));
					}
					afUserWithholdService.insertAfUserWithholdDto(userWithholdDo);
				} catch (Exception e){
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
				}
		} else {
			afUserWithholdService.updateAfUserWithholdDtoByUserId(userDo.getRid(), Integer.parseInt(IsSwitch));
		}

		AfUserWithholdDo withholdInfo = afUserWithholdService.getWithholdInfo(userDo.getRid());
		map.put("isWithhold", withholdInfo.getIsWithhold());
		resp.setResponseData(map);
		return resp;
	}
	
}
