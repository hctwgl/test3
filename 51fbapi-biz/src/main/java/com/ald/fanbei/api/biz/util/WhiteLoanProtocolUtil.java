package com.ald.fanbei.api.biz.util;


import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author 郭帅强 2017年1月16日 下午11:44:10
 * @类描述：用户工具类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class WhiteLoanProtocolUtil {

    @Resource
    AfResourceService afResourceService;

    public List<AfResourceDo> getProtocolList(String type, Map map) {
        List<AfResourceDo> afResourceDoList = afResourceService.getConfigByTypes(Constants.WHITE_AGREEMENT);
        List<AfResourceDo> resourceDoList = new ArrayList<>();
        String userName = ObjectUtils.toString(map.get("userName"), "").toString();
        Long loanId = NumberUtil.objToLongDefault(map.get("loanId"), 0l);
        Long loanRemark = NumberUtil.objToLongDefault(map.get("loanRemark"), 0l);
        int repayRemark = NumberUtil.objToIntDefault(map.get("repayRemark"), 0);
        int nper = NumberUtil.objToIntDefault(map.get("nper"), 0);
        BigDecimal amount = NumberUtil.objToBigDecimalDefault(map.get("amount"), BigDecimal.ZERO);
        BigDecimal totalServiceFee = NumberUtil.objToBigDecimalDefault(map.get("totalServiceFee"), BigDecimal.ZERO);
        BigDecimal overdueRate = NumberUtil.objToBigDecimalDefault(map.get("overdueRate"), new BigDecimal(0));
        BigDecimal serviceRate = NumberUtil.objToBigDecimalDefault(map.get("serviceRate"), new BigDecimal(0));
        BigDecimal interestRate = NumberUtil.objToBigDecimalDefault(map.get("interestRate"), new BigDecimal(0));
        if (afResourceDoList.size() == 0){
            return resourceDoList;
        }
        for (AfResourceDo afResourceDo : afResourceDoList) {
            if (afResourceDo.getValue1().contains(type)) {
                if ("WHITE_LOAN_CONTRACT".equals(afResourceDo.getSecType())) {
                    afResourceDo.setValue("/fanbei-web/h5/whiteLoanProtocol?userName=" +userName+
                    "&amount="+amount+"&nper="+nper+"&loanId="+loanId+"&loanRemark="+loanRemark+
                    "&repayRemark="+repayRemark);
                }
                else if ("WHITE_PLATFORM_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())){
                    afResourceDo.setValue("/fanbei-web/h5/whiteLoanPlatformServiceProtocol?userName=" +userName+
                            "&totalServiceFee="+totalServiceFee+"&loanId="+loanId+"&overdueRate="+overdueRate
                            +"&serviceRate="+serviceRate+"&interestRate="+interestRate);
                }
                resourceDoList.add(afResourceDo);
            }
        }
        return resourceDoList;
    }

}
