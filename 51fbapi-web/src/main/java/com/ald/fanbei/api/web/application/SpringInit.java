package com.ald.fanbei.api.web.application;

import com.ald.fanbei.api.biz.foroutapi.FBRPCServer;
import com.ald.fanbei.api.biz.foroutapi.service.HomeBorrowService;
import com.ald.fanbei.api.common.util.SpringBeanContextUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author honghzengpei 2017/10/20 14:38
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SpringInit implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        HomeBorrowService homeBorrowService =  (HomeBorrowService)SpringBeanContextUtil.getBean("afBorrowService");
        FBRPCServer server = new FBRPCServer() ;
        server.registService(homeBorrowService) ;
        server.startServer(9303) ;
    }
}
