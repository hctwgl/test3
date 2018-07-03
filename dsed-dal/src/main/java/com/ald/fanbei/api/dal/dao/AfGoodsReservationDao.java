/**
 * 
 */
package com.ald.fanbei.api.dal.dao;
 
import com.ald.fanbei.api.dal.domain.AfGoodsReservationDo;
 
/**
 * @类描述：
 * 活动商品预约信息Dao
 * @author chengkang 2017年6月8日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsReservationDao {
 
    /**
    * 增加记录
    * @param addGoodsReservation
    * @return
    */
    int addGoodsReservation(AfGoodsReservationDo afGoodsReservationDo);
   /**
    * 更新记录
    * @param updateGoodsReservation
    * @return
    */
    int updateGoodsReservation(AfGoodsReservationDo afGoodsReservationDo);
 
    /**
     * 根据过滤条件获取用户预约次数
     * @param afGoodsReservationDo
     * @return
     */
    Integer getRevCountNumsByQueryCondition(AfGoodsReservationDo afGoodsReservationDo);
    
    /**
     * 根据活动name获取活动id
     * @param name
     * 
     * @return 
     * 
     * **/
    long getActivityIdByname(String name);
    
    /**
     * 根据userId得到预约状态
     * @param userId
     * @return
     */
    String getGoodsReservationStatusByUserId(long userId);
}