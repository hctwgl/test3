package com.ald.fanbei.common;

/**
 * 
 *@类ImageFormat.java 的实现描述：类ImageFormat.java的实现描述：图片规格 
 *@author 陈金虎 2017年1月16日 下午11:25:52
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ImageFormat {

    // width start
    private int    X;

    // height start
    private int    Y;

    private int    width;

    private int    height;

    private String imageSuffix;

    public ImageFormat() {
    }
    
    /**
     * 
     * @param x 起始宽
     * @param y 起始高
     * @param width 截取的宽度
     * @param height 截取的高度
     * @param suffix 图片后缀
     */
    public ImageFormat(int x, int y, int width, int height, String suffix) {
        this.X = x;
        this.Y = y;
        this.width = width;
        this.height = height;
        this.imageSuffix = suffix;
    }

    public int getX() {
        return X;
    }

    public void setX(int x) {
        X = x;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }

    public void setImageSuffix(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

}
