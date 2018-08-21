package com.ald.fanbei.api.dal.domain.supplier;


import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AfSolrSearchResultDo extends AbstractSerial {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7837686942717437190L;

	private List<Long> goodsIds;
	private Integer totalCount;
	private Integer totalPage;
	
}
