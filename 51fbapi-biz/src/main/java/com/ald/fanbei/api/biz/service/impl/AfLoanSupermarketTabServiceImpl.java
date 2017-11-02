package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfLoanSupermarketTabService;
import com.ald.fanbei.api.dal.dao.AfLoanSupermarketTabDao;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketTabDo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 沈铖 2017/7/5 下午4:15
 * @类描述: AfLoanSupermarketTabServiceImpl
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("AfLoanSupermarketTabService")
public class AfLoanSupermarketTabServiceImpl implements AfLoanSupermarketTabService{
    @Resource
    private AfLoanSupermarketTabDao afLoanSupermarketTabDao;

    @Override
    public List<AfLoanSupermarketTabDo> getTabList() {
        AfLoanSupermarketTabDo afLoanSupermarketTabDo = new AfLoanSupermarketTabDo();
        return afLoanSupermarketTabDao.getListByCommonCondition(afLoanSupermarketTabDo);
    }
}
