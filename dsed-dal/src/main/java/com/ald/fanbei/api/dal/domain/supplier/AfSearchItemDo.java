package com.ald.fanbei.api.dal.domain.supplier;


import com.ald.fanbei.api.common.AbstractSerial;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AfSearchItemDo extends AbstractSerial {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = -6506298019301987874L;
	
	private String goodsName;
	private Long goodsId;
	private String categoryName;
	
}
