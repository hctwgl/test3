package com.ald.fanbei.api.common;

import java.io.InputStream;

/**
 * 
 *@类ImageBean.java 的实现描述：类ImageBean.java的实现描述：图片bean，包括图片的流和大小
 *@author 陈金虎 2017年1月16日 下午11:25:31
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ImageBean extends AbstractSerial {

    private static final long serialVersionUID = 4330830078903148574L;

	private InputStream       inputStream;

    private int               size;

    public ImageBean() {
    }

    public ImageBean(InputStream is, int size) {
        this.inputStream = is;
        this.size = size;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
