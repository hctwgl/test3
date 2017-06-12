package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfLoanSupermarketService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.dal.dao.AfLoanSupermarketDao;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;

/**
 * @类描述：
 * 贷款超市Service实现
 * @author chengkang 2017年6月3日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afLoanSupermarketService")
public class AfLoanSupermarketServiceImpl extends BaseService implements AfLoanSupermarketService {
	
	@Resource
	AfLoanSupermarketDao afLoanSupermarketDao;
	
	@Override
	public int addLoanSupermarket(AfLoanSupermarketDo afLoanSupermarketDo){
		return afLoanSupermarketDao.addLoanSupermarket(afLoanSupermarketDo);
	}
   
	@Override
	public int updateLoanSupermarket(AfLoanSupermarketDo afLoanSupermarketDo){
		return afLoanSupermarketDao.updateLoanSupermarket(afLoanSupermarketDo);
	}

	@Override
	public List<AfLoanSupermarketDo> getLoanSupermarketListOrderNo(Integer start){
		return afLoanSupermarketDao.getLoanSupermarketListOrderNo(start);
	}
	
	@Override
	public AfLoanSupermarketDo getLoanSupermarketByLsmNo(String lsmNo){
		return afLoanSupermarketDao.getLoanSupermarketByLsmNo(lsmNo);
	}
}
