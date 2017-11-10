package com.ald.fanbei.api.biz.util;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

public class PdfCreateUtil {

    public static void create(Map<String, Object> data) throws IOException, DocumentException {
//        String fileName = "F:/doc/1.pdf"; // pdf模板
//        String fileName = "F:/doc/t.pdf"; // pdf模板
        String templatePath = data.get("templatePath").toString();//模板路径
        String newPDFPath = data.get("PDFPath").toString();//生成路径
        PdfReader reader = new PdfReader(templatePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
            /* 将要生成的目标PDF文件名称 */
        PdfStamper ps = new PdfStamper(reader, bos);
        PdfContentByte under = ps.getUnderContent(1);
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
        OutputStream fos = new FileOutputStream(newPDFPath);
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();
        bos.close();
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

}
