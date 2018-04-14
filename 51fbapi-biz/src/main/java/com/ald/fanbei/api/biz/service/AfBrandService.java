package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBrandDo;
import com.ald.fanbei.api.dal.domain.dto.AfBrandDto;

/**
 * 品牌Service
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-04-10 19:29:42
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBrandService extends ParentService<AfBrandDo, Long>{
	/**
	 * 获取所有品牌并按照首字母进行排序
	 * @return
	 */
	List<AfBrandDto> getAllAndNameSort();
	/**
	 * 查询配置的热卖品牌列表
	 * @param brandIds
	 * @return
	 */
	List<AfBrandDo> getHotBrands(String[] brandIds);

}
