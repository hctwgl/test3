package com.ald.fanbei.api.common.util;

import java.awt.Image;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 *@类描述：文件尺寸处理工具类
 *@author 陈金虎 2017年1月16日 下午11:41:31
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FileSizeUtil {
	private static final Logger logger = LoggerFactory.getLogger(FileSizeUtil.class);
	
	private static final String[] IMAGE_SUFFIX             = new String[]{".jpg",".jpeg",".gif",".png",".bmp"};
	/**
	 * 通过文件名检测是否是图片
	 * @param fileName
	 * @return
	 */
	public static boolean checkIfIsImage(String fileName){
		boolean result = false;
		if(StringUtils.isBlank(fileName)){
			return result;
		}
		for(String item:IMAGE_SUFFIX){
			if(fileName.endsWith(item)){
				return true;
			}
		}
		return result;
	}
	
    /**
     * 获得图片的宽高，[0]=宽，[1]=高
     * 
     * @param is
     * @return
     */
    public static int[] getImageWidthHeight(InputStream is){
    	int height = -1;
    	int width = -1;
    	int[] widthHeig = new int[2];
    	try{
    		Image image = ImageIO.read(is);
    		ImageIcon imageIcon = new ImageIcon(image);
    		height = imageIcon.getIconHeight();
    		width = imageIcon.getIconWidth();
    	}catch(Exception e){
    		logger.error("计算图片宽高失败！");
    	}
    	widthHeig[0] = width;
    	widthHeig[1] = height;
    	return widthHeig;
    }
	
	public static void main(String[] args) {
	    System.out.println(checkIfIsImage("dsasdsjpg"));
    }
	
	
	
}
