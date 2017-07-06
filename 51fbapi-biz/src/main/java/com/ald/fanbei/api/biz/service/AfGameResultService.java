package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfGameResultDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
/**
 * 
 *@类现描述：
 *@author chenjinhu 2017年6月4日 下午3:13:08
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGameResultService {
    /**
     * 获取最近中奖纪录
     * @return
     */
    List<AfGameResultDo> getLatestRecord();
    
    /**
     * 处理抓娃娃结果
     * @param userInfo 用户信息
     * @param catchResult 抓娃娃结果
     * @param item 娃娃编号
     * @param code 抽奖码
     */
    AfGameResultDo dealWithResult(AfUserDo userInfo,String catchResult,String item,String code);
    
    /**
     * 游戏结果
     * @param gameId 游戏Id
     * @param user 用户信息
     * @param couponId 优惠券Id
     * @param lotteryResult 结果
     * @return
     */
    AfGameResultDo addGameResult(Long gameId,AfUserDo user,Long couponId,String lotteryResult);

	List<AfGameResultDo> getTearPacketResultByUserId(Long userId);
}
