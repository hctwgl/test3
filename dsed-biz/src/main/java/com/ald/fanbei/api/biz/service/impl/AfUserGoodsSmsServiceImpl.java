/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfActivityModelService;
import com.ald.fanbei.api.biz.service.AfUserGoodsSmsService;
import com.ald.fanbei.api.dal.dao.AfActivityModelDao;
import com.ald.fanbei.api.dal.dao.AfUserGoodsSmsDao;
import com.ald.fanbei.api.dal.domain.AfActivityModelDo;
import com.ald.fanbei.api.dal.domain.AfUserGoodsSmsDo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @类描述：
 * @author 江荣波 2017年6月20日下午4:47:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserGoodsSmsService")
public class AfUserGoodsSmsServiceImpl implements AfUserGoodsSmsService {

	@Resource
	AfUserGoodsSmsDao afUserGoodsSmsDao;

	@Override
	public AfUserGoodsSmsDo selectByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo){
		return afUserGoodsSmsDao.selectByGoodsIdAndUserId(afUserGoodsSmsDo);
	}

	@Override
	public int insertByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo){
		return afUserGoodsSmsDao.insertByGoodsIdAndUserId(afUserGoodsSmsDo);
	}

	@Override
	public AfUserGoodsSmsDo selectBookingByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo){
		return afUserGoodsSmsDao.selectBookingByGoodsIdAndUserId(afUserGoodsSmsDo);
	}
	
}
