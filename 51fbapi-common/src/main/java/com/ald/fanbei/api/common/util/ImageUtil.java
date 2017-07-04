package com.ald.fanbei.api.common.util;

import com.ald.fanbei.api.common.ImageBean;
import com.ald.fanbei.api.common.ImageFormat;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * 
 *@类描述：图片处理类，包括截图和压缩图片
 *@author 陈金虎 2017年1月16日 下午11:42:03
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ImageUtil {

    protected final static Logger logger = LoggerFactory.getLogger(ImageUtil.class);

    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();
    static {
        // images
        mFileTypes.put("FFD8FF", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");
        //
        mFileTypes.put("41433130", "dwg"); // CAD
        mFileTypes.put("38425053", "psd");
        mFileTypes.put("7B5C727466", "rtf"); // 日记本
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        mFileTypes.put("44656C69766572792D646174653A", "eml"); // 邮件
        mFileTypes.put("D0CF11E0", "doc");
        mFileTypes.put("5374616E64617264204A", "mdb");
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462D312E", "pdf");
        mFileTypes.put("504B0304", "docx");
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("1F8B08", "gz");
    }
    
    
    public static String getFileType(InputStream is) throws IOException{
        byte[] b = new byte[4];
        is.read(b, 0, b.length);
        String value = bytesToHexString(b);
        String type = mFileTypes.get(value);
        return type == null?"jpg":type;
    }
    
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        System.out.println(builder.toString());
        return builder.toString();
    }
    
    /**
     * 根据文件路径重新生成文件名，文件名后缀为oldFilePath的后缀
     * 
     * @param oldFilePath
     * @return
     */
    public static String getFileName(String oldFilePath){
        String newFileName = DigestUtil.MD5_16(UUID.randomUUID().toString());
        if(oldFilePath.lastIndexOf(".") >=0){
            newFileName = newFileName + oldFilePath.substring(oldFilePath.lastIndexOf("."));
        }
        return newFileName;
    }
    
    /**
     * 原文件名后，文件后缀之前加上tail，如oldFileName=hello.jpg,tail=_big,则返回hello_big.jpg
     * 
     * @param oldFilePath
     * @return
     */
    public static String getFileName(String oldFileName,String tail){
        String newFileName = oldFileName;
        if(oldFileName.lastIndexOf(".") >=0){
            newFileName = oldFileName.substring(0,oldFileName.lastIndexOf("."));
            newFileName = newFileName + tail + oldFileName.substring(oldFileName.lastIndexOf("."));
        }else{
            newFileName = newFileName + tail;
        }
        return newFileName;
    }
    
    /**
     * 截图，把is截成规格为format的图片
     * 
     * @param is
     * @param format
     * @return
     */
    public static ImageBean cutImage(InputStream is, ImageFormat format) {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(format.getImageSuffix());
        ImageReader reader = readers.next();
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(is);
            logger.debug("iis length is {}", iis == null ? 0 : iis.length());
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(format.getX(), format.getY(), format.getWidth(), format.getHeight());
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bi, format.getImageSuffix(), os);
            InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
            ImageBean ssImage = new ImageBean(inputStream, os.size());
            return ssImage;
        } catch (IOException e) {
            logger.error("cut the image input faild, exception is {}", e);
        } finally {
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException e) {
                    logger.error("image input stream close failed,exception is {}", e);
                }
                iis = null;
            }
        }
        return new ImageBean();
    }
    
    /**
     * 把原图片压缩成w*h的图片
     * 
     * @param is 原始图片流
     * @param new_w 需要压缩到的宽度
     * @param new_h 需要压缩到的高度
     * @param per  百分比
     * @return
     */
    public static ImageBean compressToTargetImage(InputStream is, int new_w, int new_h, float per) {
        try {
            Image src = javax.imageio.ImageIO.read(is);
            BufferedImage tag = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
            // tag.getGraphics().drawImage(src,0,0,new_w,new_h,null);
            // 绘制缩小后的图
            tag.getGraphics().drawImage(src.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);
            ByteArrayOutputStream newimage = new ByteArrayOutputStream(); // 输出到文件流
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(tag);
            /* 压缩质量 */
            jep.setQuality(per, true);
            encoder.encode(tag, jep);

            InputStream inputStream = new ByteArrayInputStream(newimage.toByteArray());
            newimage.close();
            ImageBean result = new ImageBean(inputStream, newimage.size());
            return result;
        } catch (IOException ex) {
            logger.error("compress image error", ex);
            return null;
        }
    }

    /**
     * 生成随机验证码图片
     * @return
     */
    public static Map<String, BufferedImage> createRandomImage() {
        Map<String, BufferedImage> result = new HashMap<>();
        int width = 258;//图片的width
        int height = 100;//图片的height
        int codeCount = 4;//图片上显示验证码的个数
        int fontHeight = 50;
        int xx = 45;
        int codeY = 60;
        char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gd = bufferedImage.getGraphics();
        //随机数生成器类
        Random random = new Random();
        //将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);

        //设置字体
        Font font = new Font("Default", Font.BOLD, fontHeight);
        gd.setFont(font);

        //随机产生30条干扰线
        gd.setColor(Color.BLACK);
        for (int i = 0; i < 30; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }

        //randomCode用于保存随机产生的验证码
        StringBuffer randomCode = new StringBuffer();
        int red = 0;
        int green = 0;
        int blue = 0;
        for (int i = 0; i < codeCount; i++) {
            //得到随机产生的验证码数字。
            String code = String.valueOf(codeSequence[random.nextInt(62)]);
            //产生随机的颜色分量来构造颜色值
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            //用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * xx, codeY);

            //将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        result.put(randomCode.toString(), bufferedImage);

        return result;
    }

}
