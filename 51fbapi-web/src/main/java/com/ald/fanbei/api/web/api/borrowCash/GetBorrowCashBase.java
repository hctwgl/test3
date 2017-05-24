/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月25日上午11:35:07
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class GetBorrowCashBase {

	public Map<String, Object> getObjectWithResourceDolist(List<AfResourceDo> list) {
		Map<String, Object> data = new HashMap<String, Object>();

		for (AfResourceDo afResourceDo : list) {
			if (StringUtils.equals(afResourceDo.getType(), AfResourceType.borrowRate.getCode())) {
				if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashRange.getCode())) {

					data.put("maxAmount", afResourceDo.getValue());
					data.put("minAmount", afResourceDo.getValue1());

				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashBaseBankDouble.getCode())) {
					data.put("bankDouble", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashPoundage.getCode())) {
					data.put("poundage", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashOverduePoundage.getCode())) {
					data.put("overduePoundage", afResourceDo.getValue());

				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BaseBankRate.getCode())) {
					data.put("bankRate", afResourceDo.getValue());
				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.borrowCashSupuerSwitch.getCode())) {
					data.put("supuerSwitch", afResourceDo.getValue());
				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.borrowCashLenderForCash.getCode())) {
					data.put("lender", afResourceDo.getValue());
					
				} else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.borrowCashTotalAmount.getCode())) {
					data.put("amountPerDay", afResourceDo.getValue());
				}else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.borrowCashShowNum.getCode())) {
					data.put("nums", afResourceDo.getValue());
				}



			} else {
				if (StringUtils.equals(afResourceDo.getType(), AfResourceSecType.BorrowCashDay.getCode())) {
					data.put("borrowCashDay", afResourceDo.getValue());

				}
			}
		}

		// rate.put("overduePoundage", data.get("overduePoundage"));
		// rate.put("bankService", bankService);
		// rate.put("poundage", data.get("poundage"));
		// rate.put("maxAmount", data.get("maxAmount"));
		// rate.put("minAmount", data.get("minAmount"));
		// rate.put("borrowCashDay", data.get("borrowCashDay"));

		return data;

	}

	public List<Object> getBannerObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
		List<Object> bannerList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : bannerResclist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			data.put("type", afResourceDo.getValue1());
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());

			bannerList.add(data);
		}
		return bannerList;
	}
}
