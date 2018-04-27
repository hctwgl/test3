package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfThirdAnnivCelebrationService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.common.enums.UserAccountSceneType;
import com.ald.fanbei.api.dal.domain.AfInterimAuDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author luoxiao @date 2018/4/19 19:17
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afThirdAnnivCelebrationService")
public class AfThirdAnnivCelebrationServiceImpl implements AfThirdAnnivCelebrationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    private AfBorrowBillService afBorrowBillService;

    @Override
    public void getUserAuAmountInfo(AfUserDo userDo, Map<String, Object> data){
        AfUserAccountSenceDo userAccountInfo = new AfUserAccountSenceDo();
        try{
            if(userDo!=null){
                userAccountInfo = afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), userDo.getRid());
                if(userAccountInfo==null){
                    userAccountInfo = new AfUserAccountSenceDo();
                    userAccountInfo.setAuAmount(new BigDecimal(0));
                    userAccountInfo.setUsedAmount(new BigDecimal(0));
                }else{
                    // 通过强风控审核
                    // 授予的额度
                    BigDecimal onlineAmount = userAccountInfo.getAuAmount().subtract(userAccountInfo.getUsedAmount());
                    // 临时额度
                    AfInterimAuDo interimAuDo = afBorrowBillService.selectInterimAmountByUserId(userDo.getRid());
                    if (interimAuDo != null
                            && interimAuDo.getGmtFailuretime().getTime() > new Date().getTime()) {
                        onlineAmount = onlineAmount.add(interimAuDo.getInterimAmount()).subtract(interimAuDo.getInterimUsed());
                    }
                    if(onlineAmount.compareTo(BigDecimal.ZERO)<0){
                        onlineAmount = BigDecimal.ZERO;
                    }
                    userAccountInfo.setAuAmount(onlineAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
                }
            }else{
                userAccountInfo.setAuAmount(new BigDecimal(0));
                userAccountInfo.setUsedAmount(new BigDecimal(0));
            }
        }catch (Exception e){
            logger.error("partActivityInfoV2 get account error for:" + e);
        }
        data.put("userAccountInfo", userAccountInfo);
    }
}
