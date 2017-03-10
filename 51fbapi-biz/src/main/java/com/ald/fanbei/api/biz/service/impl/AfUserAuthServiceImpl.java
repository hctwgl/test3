package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月15日 下午3:09:39
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAuthService")
public class AfUserAuthServiceImpl implements AfUserAuthService {

	@Resource
	AfUserAuthDao afUserAuthDao;
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Override
	public int addUserAuth(AfUserAuthDo afUserAuthDo) {
		return afUserAuthDao.addUserAuth(afUserAuthDo);
	}

	@Override
	public int updateUserAuth(AfUserAuthDo afUserAuthDo) {
		return afUserAuthDao.updateUserAuth(afUserAuthDo);
	}

	@Override
	public AfUserAuthDo getUserAuthInfoByUserId(Long userId) {
		return afUserAuthDao.getUserAuthInfoByUserId(userId);
	}

	@Override
	public String getConsumeStatus(Long userId) {
		AfUserAuthDo auth = afUserAuthDao.getUserAuthInfoByUserId(userId);
		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(userId);
		String status = YesNoStatus.NO.getCode();
		if(account.getAuAmount().compareTo(BigDecimal.ZERO)>0){
			if(StringUtil.equals(YesNoStatus.YES.getCode(), auth.getIvsStatus())//反欺诈分已验证
					&&StringUtil.equals(YesNoStatus.YES.getCode(), auth.getZmStatus())//反欺诈分已验证
						&&StringUtil.equals(YesNoStatus.YES.getCode(), auth.getTeldirStatus())){//通讯录匹配状态
				status = YesNoStatus.YES.getCode();
			}
		}
		return status;
	}

}
