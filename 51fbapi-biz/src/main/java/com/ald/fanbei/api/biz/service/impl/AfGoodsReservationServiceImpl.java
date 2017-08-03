package com.ald.fanbei.api.biz.service.impl;
 
import javax.annotation.Resource;
 
import org.springframework.stereotype.Service;
 
import com.ald.fanbei.api.biz.service.AfGoodsReservationService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.dal.dao.AfGoodsReservationDao;
import com.ald.fanbei.api.dal.domain.AfGoodsReservationDo;
 
/**
 * @类描述：
 * 活动商品预约Service实现
 * @author chengkang 2017年6月8日下午3:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afGoodsReservationService")
public class AfGoodsReservationServiceImpl extends BaseService implements AfGoodsReservationService {
    
    @Resource
    AfGoodsReservationDao afGoodsReservationDao;
    
    @Override
    public int addGoodsReservation(AfGoodsReservationDo afGoodsReservationDo){
        return afGoodsReservationDao.addGoodsReservation(afGoodsReservationDo);
    }
    
    @Override
    public int updateGoodsReservation(AfGoodsReservationDo afGoodsReservationDo){
        return afGoodsReservationDao.updateGoodsReservation(afGoodsReservationDo);
    }
 
    @Override
    public Integer getRevCountNumsByQueryCondition(AfGoodsReservationDo afGoodsReservationDo){
        return afGoodsReservationDao.getRevCountNumsByQueryCondition(afGoodsReservationDo);
    }
}