package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.dao.AfUserGoodsSmsDao;
import com.ald.fanbei.api.dal.domain.AfActivityGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserGoodsSmsDo;
import com.ald.fanbei.api.dal.domain.dto.AfActivityGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;

import java.util.List;

/**
 * 
 * @类描述：类目service
 * @author 江荣波 2017年2月16日下午5:24:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserGoodsSmsService {

	AfUserGoodsSmsDo selectByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo);

	int insertByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo);

	AfUserGoodsSmsDo selectBookingByGoodsIdAndUserId(AfUserGoodsSmsDo afUserGoodsSmsDo);
	
}
