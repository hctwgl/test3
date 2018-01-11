package com.ald.fanbei.api.web.vo;


import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 *@类描述：
 *@author chenqiwei 2018年1月10日上午10:09:09
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class NewbieTaskVo extends AbstractSerial {

    /**
     * 
     */
        private static final long serialVersionUID = 1L;
	
        private Integer finish; // 是否完成0：未完成，1已完成
        private String  title;  //标题
        private String button;//按钮值
        private String url;//跳转链接
	private String value1;//扩展值1
	private String value2;//扩展值2
	private String value3;//扩展值3
	private String value4;//扩展值4
    
        public Integer getFinish() {
            return finish;
        }
        public void setFinish(Integer finish) {
            this.finish = finish;
        }
        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }
        
	
	public String getButton() {
	    return button;
	}
	public void setButton(String button) {
	    this.button = button;
	}
	public String getUrl() {
	    return url;
	}
	public void setUrl(String url) {
	    this.url = url;
	}
	public String getValue1() {
	    return value1;
	}
	public void setValue1(String value1) {
	    this.value1 = value1;
	}
	public String getValue2() {
	    return value2;
	}
	public void setValue2(String value2) {
	    this.value2 = value2;
	}
	public String getValue3() {
	    return value3;
	}
	public void setValue3(String value3) {
	    this.value3 = value3;
	}
	public String getValue4() {
	    return value4;
	}
	public void setValue4(String value4) {
	    this.value4 = value4;
	}
        

}
