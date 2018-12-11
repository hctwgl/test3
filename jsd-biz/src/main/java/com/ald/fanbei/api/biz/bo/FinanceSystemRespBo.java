package com.ald.fanbei.api.biz.bo;


/**
 *
 * @类描述：清结算系统请求响应爱上街结果
 * @author fanmanfu 2018年11月02日下午8:41:40
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FinanceSystemRespBo {

    public String code;
    public String msg;
    public String data;
    public String sign;
    public String timestamp;
    public String dataType;//数据类型
    private String company; //清算公司


    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
        public String getMsg() {
            return msg;
        }
        public void setMsg(String msg) {
            this.msg = msg;
        }
        public String getData() {
            return data;
        }
        public void setData(String data) {
            this.data = data;
        }
        public String getSign() {
            return sign;
        }
        public void setSign(String sign) {
            this.sign = sign;
        }
        public String getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "CollectionSystemReqRespBo [code=" + code + ", msg=" + msg
                    + ", data=" + data + ", sign=" + sign + ", timestamp="
                    + timestamp + "]";
        }
}
