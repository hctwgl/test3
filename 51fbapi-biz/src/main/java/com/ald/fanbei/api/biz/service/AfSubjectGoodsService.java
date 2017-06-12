package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfSubjectGoodsDo;
import com.ald.fanbei.api.dal.domain.query.AfSubjectGoodsQuery;
/**
 * 
 * @类描述：
 * @author 江荣波 2017年3月2日上午11:27:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSubjectGoodsService {
	
	int addSubjectGoods(AfSubjectGoodsDo afSubjectGoodsDo);

	List<AfGoodsDo> listAllSubjectGoods(AfSubjectGoodsQuery query);
	
	public List<AfGoodsDo> listQualitySubjectGoods();
}
