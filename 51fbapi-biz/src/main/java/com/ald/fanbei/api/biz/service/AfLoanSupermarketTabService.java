package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfLoanSupermarketTabDo;

import java.util.List;

/**
 * @author 沈铖 2017/7/5 下午4:13
 * @类描述: AfLoanSupermarketTabService
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanSupermarketTabService {

    /**
     * 获得所有标签
     * @return
     */
    List<AfLoanSupermarketTabDo> getTabList();
}
