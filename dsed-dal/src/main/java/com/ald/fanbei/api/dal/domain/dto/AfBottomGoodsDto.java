/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfBottomGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import lombok.Getter;
import lombok.Setter;

/**
 * 页面底部商品Dto
 *
 * @author wangli
 * @date 2018/4/11 11:05
 */
@Getter
@Setter
public class AfBottomGoodsDto extends AfBottomGoodsDo {

	// 关联商品对象
	private AfGoodsDo goodsDo;
}
