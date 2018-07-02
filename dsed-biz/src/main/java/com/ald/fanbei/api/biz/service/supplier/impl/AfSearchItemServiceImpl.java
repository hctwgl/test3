package com.ald.fanbei.api.biz.service.supplier.impl;


import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.supplier.AfSearchItemService;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.supplier.AfSolrSearchResultDo;


/**
 * solr
 *
 * @author chenxuankai
 * @version 1.0.0 初始化
 * @date 2018年4月2日16:45:16
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afSearchItemService")
public class AfSearchItemServiceImpl implements AfSearchItemService {
	
	protected static final Logger logger = LoggerFactory.getLogger(AfSearchItemServiceImpl.class);

	@Autowired
	private SolrServer solrServer;
	
	@Override
	public AfSolrSearchResultDo getSearchList(String keyword, Integer pageNo, Integer pageSize) {
		try {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(keyword);
			solrQuery.set("df", "item_keywords");
			solrQuery.setStart((pageNo - 1) * pageSize);
			solrQuery.setRows(pageSize);
			QueryResponse response = solrServer.query(solrQuery);
			SolrDocumentList resultList = response.getResults();
			Integer numFound = (int) resultList.getNumFound();
			Integer pageCount = numFound / pageSize;
			if (numFound % pageSize > 0) {
				pageCount++;
			}
			List<Long> goodsIds = new ArrayList<>();
			for (SolrDocument result : resultList) {
				Long id = NumberUtil.objToLong(result.get("id"));
				goodsIds.add(id);
			}
			AfSolrSearchResultDo afSolrSearchResultDo = new AfSolrSearchResultDo();
			afSolrSearchResultDo.setGoodsIds(goodsIds);
			afSolrSearchResultDo.setTotalCount(numFound);
			afSolrSearchResultDo.setTotalPage(pageCount);
			return afSolrSearchResultDo;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("solrServer getSearchList with exception:" + e);
			return null;
		}
	}	

	
	
}
