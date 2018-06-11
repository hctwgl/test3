package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfSignRewardDo;
import com.ald.fanbei.api.dal.domain.AfSignRewardWithdrawDo;
import com.ald.fanbei.api.dal.domain.dto.AfSignRewardWithdrawDto;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 *@类描述：AfUserAccountQuery
 *@author 何鑫 2017年3月30日  10:33:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class AfSignRewardWithdrawQuery extends Page<AfSignRewardWithdrawDo>{

	private static final long serialVersionUID = -722303985401230132L;

	private Long userId;

}
