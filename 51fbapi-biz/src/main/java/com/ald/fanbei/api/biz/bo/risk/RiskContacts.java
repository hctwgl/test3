package com.ald.fanbei.api.biz.bo.risk;

import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSON;

/**
 * 
 * @类描述：通讯录信息同步
 * @author fmai 2017年6月8日 09:58:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class RiskContacts extends RiskRegisterStrongReqBo {

	private static final long serialVersionUID = 1L;

	public RiskContacts(String consumerNo, String event, String directory) {
		super(consumerNo, event, null, null, null, null, null, null, null, null, null, null, directory, null);
	}

	@Override
	protected void create(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost) {
		setConsumerNo(consumerNo);
		setEvent(event);

		setDirectory(JSON.toJSONString(StringUtil.filterEmoji(directory)));
	}
}
