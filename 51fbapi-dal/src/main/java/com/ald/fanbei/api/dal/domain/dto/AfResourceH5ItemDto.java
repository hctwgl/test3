/*
 *@Copyright (c) 2016, 浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfResourceH5Do;
import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 
 * @类描述：
 * @author 江荣波 2017年6月21日下午2:23:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class AfResourceH5ItemDto extends AfResourceH5ItemDo {

    private List goodsList;
	
}
