package com.ald.fanbei.api.common.util.dingding;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 钉钉消息数据包
 */
public class DingdingData {
    public static DingdingData create(String content) {
        DingdingData data = new DingdingData();
        data.setMsgtype("text");
        data.setText(new DingDingDataContent(content));
        return data;
    }

    private String msgtype;
    private DingDingDataContent text;
    private DingDingAt at;

    public DingDingDataContent getText() {
        return text;
    }

    public void setText(DingDingDataContent text) {
        this.text = text;
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public DingDingAt getAt() {
        return at;
    }

    public void setAt(DingDingAt at) {
        this.at = at;
    }

//    public DingdingData setAtMobiles(String[] atMobiles) {
//        this.at = new DingDingAt(atMobiles);
//        String atStr = String.join(" ", Arrays.stream(atMobiles).map(m -> "@" + m).collect(Collectors.toList()));
//        this.text.setContent(String.format("%s %s", this.text.getContent(), atStr));
//        return this;
//    }

    public DingdingData setIsAtAll() {
        this.at = new DingDingAt(true);
        return this;
    }

    private static class DingDingDataContent {
        private String content;

        public DingDingDataContent(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    private static class DingDingAt {

        public DingDingAt(String[] atMobiles) {
            this.atMobiles = atMobiles;
        }

        public DingDingAt(boolean isAtAll) {
            this.isAtAll = isAtAll;
        }

   
        private String[] atMobiles;

        private Boolean isAtAll;

        public String[] getAtMobiles() {
            return atMobiles;
        }

        public void setAtMobiles(String[] atMobiles) {
            this.atMobiles = atMobiles;
        }

        public Boolean getAtAll() {
            return isAtAll;
        }

        public void setAtAll(Boolean atAll) {
            isAtAll = atAll;
        }
    }
}
