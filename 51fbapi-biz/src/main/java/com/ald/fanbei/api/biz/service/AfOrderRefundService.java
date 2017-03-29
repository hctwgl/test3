package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfOrderRefundDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月29日下午3:53:02
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfOrderRefundService {

	int addOrderRefund(AfOrderRefundDo orderRefundInfo);
}
