package com.ald.fanbei.api.biz.kafka;

public  class KafkaConstants {
    //region 基础配置
    public static final String SSL_TRUSTSTORE_LOCATION = "ssl.truststore.location";
    public static final String SSL_TRUSTSTORE_PASSWORD = "ssl.truststore.password";
    public static final String SECURITY_PROTOCOL = "security.protocol";
    public static final String SASL_MECHANISM= "sasl.mechanism";
    //endregion

    //region kafka 事件
    public static final String SYNC_BORROW_CASH= "sync_borrow_cash";
    public static final String SYNC_USER_BASIC_DATA= "sync_user_basic_data";
    //endregion kafka 事件

    //region kafka 主题
    public static final String SYNC_TOPIC= "PUBLIC_KAFKA_TEST1";
    //endregion kafka 主题

    //region kafka 是否打开
    public static final String KAFKA_OPEN= "KAFKA_OPEN";
    public static final String REFRESH_TIME= "refreshTime";
    //endregion

}
