package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**  
 * @Description: 都市e贷借钱信息vo
 * @author gsq
 * @date 2018年1月25日
 */
@Getter
@Setter
public class DsedPrococolVo extends AbstractSerial {
	
	private static final long serialVersionUID = 1L;
	private String protocolName;		// 协议名称
	private String protocolUrl;			//协议url


}
