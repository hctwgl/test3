package com.ald.fanbei.api.biz.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class PdfCreateUtil {

    public static void create(Map<String, Object> data,OutputStream fos,ByteArrayOutputStream bos) throws IOException, DocumentException {
            String templatePath = data.get("templatePath").toString();//模板路径
            String newPDFPath = data.get("PDFPath").toString();//生成路径
            PdfReader reader = new PdfReader(templatePath);
            bos = new ByteArrayOutputStream();
            /* 将要生成的目标PDF文件名称 */
            PdfStamper ps = new PdfStamper(reader, bos);
            ps.getUnderContent(1);
	          /* 使用中文字体 */
            BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            ArrayList<BaseFont> fontList = new ArrayList<BaseFont>();
            fontList.add(bf);
            //给pdf合同赋值
            /* 取出报表模板中的所有字段 */
            AcroFields fields = ps.getAcroFields();
            fields.setSubstitutionFonts(fontList);
            fillData(fields, data);
	        /* 必须要调用这个，否则文档不会生成的 */
            ps.setFormFlattening(true);
            ps.close();
            //文件存储的路径（需要先创建好文件夹）
            fos = new FileOutputStream(newPDFPath);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            bos.close();

    }

    public static byte[] createByte(Map<String, Object> data,OutputStream fos,ByteArrayOutputStream bos) throws IOException, DocumentException {
        String templatePath = data.get("templatePath").toString();//模板路径
        PdfReader reader = new PdfReader(templatePath);
            /* 将要生成的目标PDF文件名称 */
        PdfStamper ps = new PdfStamper(reader, bos);
        ps.getUnderContent(1);
	          /* 使用中文字体 */
        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        ArrayList<BaseFont> fontList = new ArrayList<BaseFont>();
        fontList.add(bf);
        //给pdf合同赋值
            /* 取出报表模板中的所有字段 */
        AcroFields fields = ps.getAcroFields();
        fields.setSubstitutionFonts(fontList);
        fillData(fields, data);
	        /* 必须要调用这个，否则文档不会生成的 */
        ps.setFormFlattening(true);
        ps.close();
        return bos.toByteArray();
    }

    public static void fillData(AcroFields fields, Map<String, Object> data)
            throws IOException, DocumentException {
        java.util.Iterator<String> it = fields.getFields().keySet().iterator();
        while (it.hasNext()) {
            String name = it.next().toString();
            Object value = data.get(name);
            if (null == value){
                value = "";
            }
            fields.setField(name, value.toString()); // 为字段赋值,注意字段名称是区分大小写的
        }
    }
        /**
         * 从网络Url中下载文件
         * @param urlStr
         * @throws IOException
         */
        public static byte[]  downLoadByUrl(String urlStr,ByteArrayOutputStream bos) throws IOException{
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(5*1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            byte[] getData = readInputStream(inputStream,bos);
            return getData;
        }

    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static  byte[] readInputStream(InputStream inputStream,ByteArrayOutputStream bos) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
