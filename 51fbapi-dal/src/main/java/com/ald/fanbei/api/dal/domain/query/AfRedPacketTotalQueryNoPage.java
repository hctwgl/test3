package com.ald.fanbei.api.dal.domain.query;

import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用户总红包查询对象
 *
 * @author wangli
 * @date 2018/5/10 16:11
 */
@Getter
@Setter
public class AfRedPacketTotalQueryNoPage extends AfRedPacketTotalDo {

    private Date gmtWithdrawStart;

    private Date gmtWithdrawEnd;
}
