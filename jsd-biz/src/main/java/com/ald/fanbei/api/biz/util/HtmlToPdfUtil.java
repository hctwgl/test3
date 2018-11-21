package com.ald.fanbei.api.biz.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.charset.Charset;

/**
 * @author yangfeng
 * @date 2017/12/24
 * @descriptions iText生成pdf的工具类，依赖iText
 */
public final class HtmlToPdfUtil {

    private static final String FONT = "STSong-Light";
    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static Document document;
    private static PdfWriter writer = null;
    private static OutputStream outputStream;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HtmlToPdfUtil.class);
    /**
     * 初始化
     */
    private static void init(String out) throws DocumentException, FileNotFoundException {
        if(outputStream == null){
            outputStream = new FileOutputStream(out);
        }
        if(document == null){
            document = new Document();
            writer = PdfWriter.getInstance(document, outputStream);
        }
        document.open();
    }

    /**
     * 销毁方法
     */
    private static void destory(){
        if(document != null){
            document.close();
        }
        if(writer != null){
            writer.close();
        }
        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加字体
     * @param fontPath 字体路径
     */
    private static Font addFont(String fontPath){
        Font font = FontFactory.getFont("STSong-Light", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        if(!StringUtils.isEmpty(fontPath)){
            font = FontFactory.getFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        }
        return font;
    }

    /**
     * 创建pdf文件
     * @param inputStream
     * @param out
     * @throws IOException
     * @throws DocumentException
     */
    private static void createPdf(InputStream inputStream, String out) throws IOException, DocumentException {
        init(out);
        XMLWorkerFontProvider xmlWorkerFontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
        xmlWorkerFontProvider.register(FONT);
        XMLWorkerHelper xmlWorkerHelper = XMLWorkerHelper.getInstance();
        xmlWorkerHelper.parseXHtml(writer, document, inputStream, DEFAULT_CHARSET, xmlWorkerFontProvider);
        destory();
    }

    /**
     * String内容转pdf文件
     * @param context
     * @param outFilePath
     * 参考文档:http://developers.itextpdf.com/examples/font-examples/using-fonts#1227-tengwarquenya1.java
     */
    public static void stringToPdf(String context, String outFilePath){
        try {
            init(outFilePath);
            Paragraph paragraph = new Paragraph(context);
            paragraph.setFont(addFont(null));
            document.add(paragraph);
            destory();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模版路径转pdf文件
     * @param templatePath
     * @param outFilePath
     * 参考文档:http://developers.itextpdf.com/examples/xml-worker-itext5/xml-worker-examples
     */
    public static void htmlToPdf(String templatePath, String outFilePath){
        try {
            FileInputStream fileInputStream = new FileInputStream(templatePath);
            createPdf(fileInputStream, outFilePath);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模版内容转pdf文件
     * @param content
     * @param outFilePath
     * 参考文档:http://developers.itextpdf.com/examples/xml-worker-itext5/xml-worker-examples
     */
    public static void htmlContentToPdf(String content, String outFilePath){
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content.getBytes());
            createPdf(byteArrayInputStream, outFilePath);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentOperatingSystem(){
        String os = System.getProperty("os.name").toLowerCase();
        System.out.println("---------当前操作系统是-----------" + os);
        return os;
    }
    /**
     * html内容转pdf(支持CSS)
     * @param content
     * @param outFilePath
     * @param logoUrl
     * 加载文件资源,注意资源URL需要使用文件协议 “file://”
     */
    public static void htmlContentWithCssToPdf(String content, String outFilePath, String logoUrl){

        ITextRenderer render = new ITextRenderer();
        ITextFontResolver fontResolver = render.getFontResolver();
        try {
            if (null != getCurrentOperatingSystem() && getCurrentOperatingSystem().contains("windows")){
                fontResolver.addFont("c:/Windows/Fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            }else {
                fontResolver.addFont("/home/aladin/project/jsdCodeUse/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }

            // 解析html生成pdf
            render.setDocumentFromString(content);
            //解决图片相对路径的问题
            SharedContext sharedContext = render.getSharedContext();
            sharedContext.setBaseURL(logoUrl);
            render.layout();
            render.createPDF(new FileOutputStream(outFilePath));
        } catch (DocumentException e) {
            e.printStackTrace();
            logger.error("htmlContentWithCssToPdf =>{}"+e+",content = "+content+",outFilePath = "+outFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("htmlContentWithCssToPdf =>{}"+e+",content = "+content+",outFilePath = "+outFilePath);
        }
    }


}
