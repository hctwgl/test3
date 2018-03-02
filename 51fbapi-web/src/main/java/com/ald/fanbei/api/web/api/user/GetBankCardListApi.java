package com.ald.fanbei.api.web.api.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.UpsBankStatusDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述：
 * @author suweili 2017年2月22日下午8:21:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBankCardListApi")
public class GetBankCardListApi implements ApiHandle {

    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    BizCacheUtil bizCacheUtil;
    @Autowired
    AfResourceService afResourceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

	Long userId = context.getUserId();
	if (userId == null) {
	    throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
	}
	List<AfBankUserBankDto> list = afUserBankcardService.getUserBankcardByUserId(userId);

	resp.addResponseData("bankCardList", list);
	String bankcardStatus = "N";
	if (CollectionUtil.isNotEmpty(list)) {
	    for (AfBankUserBankDto item : list) {
		if (StringUtil.equals(item.getIsMain(), YesNoStatus.YES.getCode())) {
		    bankcardStatus = "Y";
		}

		// 获取银行状态（ups写入redis数据）
		String bankStatusKey = "ups_collect_" + item.getBankCode();
		Object bankStatusValue = bizCacheUtil.getObject(bankStatusKey);
		if (bankStatusValue != null && StringUtils.isNotBlank(bankStatusValue.toString())) {
		    UpsBankStatusDto bankStatus = JSON.parseObject(bankStatusValue.toString(), UpsBankStatusDto.class);
		    item.setBankStatus(bankStatus);

		    if (bankStatus.getIsMaintain() == 1) {
			AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType("CASHIER", "AP_NAME");
			item.setMessage(afResourceDo.getValue1());
			item.setIsValid("N");
		    }
		} else {
		    item.setBankStatus(new UpsBankStatusDto());
		}
	    }

	    // 集合重新排序可用状态在前不可用状态在后
	    List<AfBankUserBankDto> listMaintain = new ArrayList<AfBankUserBankDto>();
	    Iterator<AfBankUserBankDto> iterator = list.iterator();
	    while (iterator.hasNext()) {
		AfBankUserBankDto afBankUserBankDto = iterator.next();
		if ("N".equals(afBankUserBankDto.getIsValid())) {
		    // 移除维护状态的银行卡，循环结束后重新添加到集合的尾部
		    list.remove(afBankUserBankDto);
		    listMaintain.add(afBankUserBankDto);
		}
	    }
	    if (listMaintain.size() > 0) {
		// 重新添加维护状态的银行卡到集合的尾部
		for (AfBankUserBankDto item : listMaintain) {
		    list.add(item);
		}
	    }
	}

	AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);

	AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
	resp.addResponseData("realName", userAccount.getRealName());
	resp.addResponseData("bankcardStatus", bankcardStatus);
	if (StringUtil.equals(bankcardStatus, YesNoStatus.NO.getCode())) {
	    String publicKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_YOUDUN_PUBKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
	    resp.addResponseData("ydKey", publicKey);
	    resp.addResponseData("ydUrl", ConfigProperties.get(Constants.CONFKEY_YOUDUN_NOTIFY));
	    resp.addResponseData("idNumber", Base64.encodeString(userAccount.getIdNumber()));
	}
	resp.addResponseData("faceStatus", afUserAuthDo.getFacesStatus());
	return resp;
    }
}
