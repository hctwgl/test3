package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfBottomGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 *
 *
 * @author wangli
 * @date 2018/4/11 8:15
 */
@Getter
@Setter
public class AfBottomGoodsQuery extends Page<AfBottomGoodsDo>{

	// 底部商品页也米娜标识
	private String pageFlag;
}
