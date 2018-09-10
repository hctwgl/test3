package com.ald.fanbei.api.biz.util;


import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
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
public class ProtocolUtil {

    @Resource
    JsdResourceService afResourceService;

    public List<JsdResourceDo> getProtocolList(String type, Map map) {
        List<JsdResourceDo> afResourceDoList = afResourceService.listByType("AGREEMENT");
        List<JsdResourceDo> resourceDoList = new ArrayList<>();
        String userName = ObjectUtils.toString(map.get("userName"), "").toString();
        String dayType = ObjectUtils.toString(map.get("type"), "").toString();
        Long borrowId = NumberUtil.objToLongDefault(map.get("borrowId"), 0l);
        Long renewalId = NumberUtil.objToLongDefault(map.get("renewalId"), 0l);
        int renewalDay = NumberUtil.objToIntDefault(map.get("renewalDay"), 0);
        int nper = NumberUtil.objToIntDefault(map.get("nper"), 0);
        BigDecimal renewalAmount = NumberUtil.objToBigDecimalDefault(map.get("renewalAmount"), BigDecimal.ZERO);
        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(map.get("borrowAmount"), BigDecimal.ZERO);
        BigDecimal poundage = NumberUtil.objToBigDecimalDefault(map.get("poundage"), BigDecimal.ZERO);
        if (afResourceDoList.size() == 0) {
            return resourceDoList;
        }
        for (JsdResourceDo afResourceDo : afResourceDoList) {
            if (afResourceDo.getValue1().contains(type)) {
                if ("RENEWAL_CONTRACT".equals(afResourceDo.getSecType())) {//续借协议
                    afResourceDo.setValue("/jsd-web/h5/protocolLegalRenewalV2?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&renewalId=" + renewalId + "&renewalDay=" + renewalDay +
                            "&renewalAmount=" + renewalAmount);
                } else if ("INSTALMENT_CONTRACT".equals(afResourceDo.getSecType())) {//分期协议
                    afResourceDo.setValue("/jsd-web/h5/protocolLegalInstalmentV2?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&nper=" + nper + "&amount=" + borrowAmount +
                            "&poundage=" + poundage);
                } else if ("LOAN_CONTRACT".equals(afResourceDo.getSecType())) {//借款协议
                    afResourceDo.setValue("/jsd-web/h5/protocolLegalCashLoanV2?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&borrowAmount=" + borrowAmount);
                } else if ("PLATFORM_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//平台服务协议
                    afResourceDo.setValue("/jsd-web/h5/platformServiceProtocol?userName=" + userName +
                            "&type=" + dayType + "&borrowId=" + borrowId + "&poundage=" + poundage);
                } else if ("DIGITAL_CERTIFICATE_SERVICE_PROTOCOL".equals(afResourceDo.getSecType())) {//数字证书
                    afResourceDo.setValue("/jsd-web/h5/numProtocol?userName=" + userName);
                } else if ("LETTER_OF_RISK".equals(afResourceDo.getSecType())) {//风险提示协议
                    afResourceDo.setValue("/h5/sys/riskWarning");
                } else if ("BUYING_RELATED_AGREEMENTS".equals(afResourceDo.getSecType())) {//代买协议
                    afResourceDo.setValue("/jsd-web/h5/protocolAgentBuyService?userName=" + userName);
                }
                resourceDoList.add(afResourceDo);
            }
        }
        return resourceDoList;
    }

}
