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
    /**
     * 消费分期
     */
    public static final String SYNC_CONSUMPTION_PERIOD= "sync_consumption_period";
    /**
     * 借钱，现金贷，上面的那个被占用了,这个字段是风控这边的的数据使用的通知。
     */
    public static final String  SYNC_CASH_LOAN = "sync_cash_loan";
    /**
     * 每天首次登入
     */
    public static final String SYNC_FIRST_LOGIN= "sync_first_login";

    /**
     * 下单完成
     */
    public static final String SYNC_PAYED= "sync_payed";
    /**
     * 现金贷逾期
     */
    public static final String SYNV_CASH_LOAN_OVERDUE = "sync_cash_loan_overdue";
    /**
     * 现金贷逾期
     */
    public static final String SYNC_CONSUMPTION_PERIOD_OVERDUE = "sync_consumption_period_overdue";
    /**
     * 用户的基本信息
     */
    public static final String SYNC_USER_BASIC_DATA= "sync_user_basic_data";

    public static final String SYNC_SCENE_ONE= "sync_scene_one";
    //endregion kafka 事件

    //region kafka 主题
    public static final String SYNC_TOPIC= "kafka.default.topic";
    //endregion kafka 主题

    //region kafka 是否打开
    public static final String KAFKA_OPEN= "KAFKA_OPEN";
    public static final String REFRESH_TIME= "refreshTime";
    public static final String SYNC_SCENE_WEEK = "sync_scene_week";
    //endregion

}
