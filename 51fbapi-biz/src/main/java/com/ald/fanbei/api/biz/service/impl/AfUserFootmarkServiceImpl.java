package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserFootmarkService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfUserFootmarkDao;
import com.ald.fanbei.api.dal.domain.AfUserFootmarkDo;
import com.ald.fanbei.api.dal.domain.query.AfUserFootmarkQuery;
/**
 * 
 *@类描述：AfUserFootmarkServiceImpl
 *@author 何鑫 2017年1月19日  15:26:56
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserFootmarkService")
public class AfUserFootmarkServiceImpl implements AfUserFootmarkService{

	@Resource
	private AfUserFootmarkDao afUserFootmarkDao;
	
	@Override
	public int dealUserFootmark(Long userId,Long goodsId) {
		
		AfUserFootmarkDo userFootmark = afUserFootmarkDao.getUserFootmarkByUserId(userId, goodsId);
		int result =0;
		if(null == userFootmark){
			userFootmark = new AfUserFootmarkDo();
			userFootmark.setUserId(userId);
			userFootmark.setGoodsId(goodsId);
			userFootmark = buildUserFootmark(userFootmark);
			userFootmark.setBrowseTime(DateUtil.formatDate(new Date(),DateUtil.DATE_TIME_SHORT));
			result = afUserFootmarkDao.addUserFootmark(userFootmark);
		}else{
			userFootmark = buildUserFootmark(userFootmark);
			userFootmark.setBrowseTime(new StringBuffer(userFootmark.getBrowseTime()).append(",")
					.append(DateUtil.formatDate(new Date(),DateUtil.DATE_TIME_SHORT)).toString());
			result = afUserFootmarkDao.updateUserFootmark(userFootmark);
		}
		return result;
	}

	@Override
	public List<AfUserFootmarkDo> getUserFootmarkList(AfUserFootmarkQuery query) {
		return afUserFootmarkDao.getUserFootmarkList(query);
	}

	/**
	 * 获取足迹对象
	 * @param userFootmark
	 * @return
	 */
	private AfUserFootmarkDo buildUserFootmark(AfUserFootmarkDo userFootmark){
		userFootmark.setGoodsIcon("");
		userFootmark.setGoodsName("");
		userFootmark.setPriceAmount(BigDecimal.ZERO);
		userFootmark.setCommissionRate(BigDecimal.ZERO);
		userFootmark.setCommissionAmount(BigDecimal.ZERO);
		userFootmark.setSource("T");
		return userFootmark;
	}
}
