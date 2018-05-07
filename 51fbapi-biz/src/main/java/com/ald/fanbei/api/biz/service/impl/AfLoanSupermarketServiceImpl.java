package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Override
	public List<AfLoanSupermarketDo> getLoanSupermarketByLabel(String label,String systemType) {
		return afLoanSupermarketDao.getLoanSupermarketByLabel(label,systemType);
	}

	@Override
	public List<Object> getBorrowHomeListByLable(String label, String systemType) {
		List<Object> borrowList=new ArrayList<>();
		for(AfLoanSupermarketDo afLoanSupermarketDo:afLoanSupermarketDao.getLoanSupermarketByLabel(label,systemType)){
			Map<String,String> map=new HashMap<>();
			map.put("lsmNo",afLoanSupermarketDo.getLsmNo());
			map.put("iconUrl",afLoanSupermarketDo.getIconUrl());
			map.put("lsmIntro",afLoanSupermarketDo.getLsmIntro());
			map.put("lsmName",afLoanSupermarketDo.getLsmName());
			map.put("payMethod",afLoanSupermarketDo.getPayMethod()+"");
			map.put("linkUrl",afLoanSupermarketDo.getLinkUrl());
			map.put("money",afLoanSupermarketDo.getMoney()+"");
			map.put("moneyMax",afLoanSupermarketDo.getMoneyMax()+"");
			map.put("moneyMin",afLoanSupermarketDo.getMoneyMin()+"");
			map.put("registerUrl",afLoanSupermarketDo.getRegisterUrl());
			map.put("time",afLoanSupermarketDo.getTime()+"");
			map.put("timeMin",afLoanSupermarketDo.getTimeMin()+"");
			map.put("timeMax",afLoanSupermarketDo.getMoneyMax()+"");
			borrowList.add(map);
		}
		return borrowList;
	}
	
	
	@Override
	public List<Object> getLoanHomeListByLable(String label, String systemType) {
		List<Object> dcHomeList=new ArrayList<>();
		for(AfLoanSupermarketDo afLoanSupermarketDo:afLoanSupermarketDao.getLoanSupermarketByLabel(label,systemType)){
			Map<String,String> map=new HashMap<>();
			map.put("lsmNo",afLoanSupermarketDo.getLsmNo());
			map.put("iconUrl",afLoanSupermarketDo.getIconUrl());
			map.put("lsmIntro",afLoanSupermarketDo.getLsmIntro());
			map.put("lsmName",afLoanSupermarketDo.getLsmName());
			map.put("marketPoint",afLoanSupermarketDo.getMarketPoint());
			map.put("payMethod",afLoanSupermarketDo.getPayMethod()+"");
			map.put("linkUrl",afLoanSupermarketDo.getLinkUrl());
			map.put("money",afLoanSupermarketDo.getMoney()+"");
			map.put("moneyMax",afLoanSupermarketDo.getMoneyMax()+"");
			map.put("moneyMin",afLoanSupermarketDo.getMoneyMin()+"");
			map.put("registerUrl",afLoanSupermarketDo.getRegisterUrl());
			map.put("time",afLoanSupermarketDo.getTime()+"");
			map.put("timeMin",afLoanSupermarketDo.getTimeMin()+"");
			map.put("timeMax",afLoanSupermarketDo.getMoneyMax()+"");
			dcHomeList.add(map);
		}
		return dcHomeList;
	}
}
