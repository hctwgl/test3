package com.ald.fanbei.api.biz.service.impl;


import com.ald.fanbei.api.biz.service.OssFileUploadService;
import com.ald.fanbei.api.biz.util.OssUploadResult;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.FileSizeUtil;
import com.ald.fanbei.api.common.util.ImageUtil;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.UUID;


/**
 * 类OssFileUploadServiceImpl.java的实现描述：oss上传文件类
 * @author richen 2015年12月1日 下午6:11:49
 */
@Component("ossFileUploadService")
public class OssFileUploadServiceImpl implements OssFileUploadService {
    private static Logger   log           = LoggerFactory.getLogger(OssFileUploadServiceImpl.class);
    @Resource
    private OSSClient       ossClient;
    private static String OSS_BUCKET = "51fanbei-private";
    
    @Override
    public OssUploadResult uploadFileToOss(MultipartFile file) {
        String oldFileName = file.getOriginalFilename();
        String path = ConfigProperties.get(Constants.CONF_KEY_INVELOMENT_TYPE) + "/";
//        String newFileName = ImageUtil.getFileName(oldFileName);
        String contextType = "";
        if(oldFileName.lastIndexOf(".") >=0){
//            newFileName = newFileName + oldFileName.substring(oldFileName.lastIndexOf("."));
            contextType = this.getImageFileContentType(oldFileName);
        }
        return this.uploadFileToOss(file, contextType, path, oldFileName);
    }
    
    @Override
    public OssUploadResult uploadFileToOssWithName(MultipartFile file, String fileName) {
        String path = ConfigProperties.get(Constants.CONF_KEY_INVELOMENT_TYPE) + "/";
        String contextType = this.getImageFileContentType(fileName);
        return this.uploadFileToOss(file, contextType, path, fileName);
    }
    
    @Override
    public OssUploadResult uploadImageToOss(MultipartFile imageFile) {
        String fileName = imageFile.getOriginalFilename();
        String contextType = this.getImageFileContentType(fileName);
        String path = ConfigProperties.get(Constants.CONF_KEY_INVELOMENT_TYPE) + "/";
        String fileNameSuffix = UUID.randomUUID().toString();
        if(StringUtils.isNotBlank(contextType)){
            fileNameSuffix = fileNameSuffix + fileName.substring(fileName.lastIndexOf("."));
        }
        return this.uploadFileToOss(imageFile, contextType, path, fileNameSuffix);
    }
    
    @Override
    public OssUploadResult uploadImageToOss(InputStream inputStream,String fileName,int fileSize) {
        OssUploadResult result = new OssUploadResult();
        try{
            String path = ConfigProperties.get(Constants.CONF_KEY_INVELOMENT_TYPE) + "/";
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setContentType(this.getImageFileContentType(fileName));
            PutObjectResult pubResult = ossClient.putObject(OSS_BUCKET, path + fileName, inputStream,metadata);
            
            if(pubResult == null || StringUtils.isBlank(pubResult.getETag())){
                log.error("upload to oss error, put object result is null or etag is empty,  fileName {}", fileName);
                result.setSuccess(false);
                result.setMsg("upload to oss error, put object result is null or etag is empty");
                return result;
            }
            result.setSuccess(true);
            result.setMsg("upload inputStream to oss succeed");
            result.setFileMd5(pubResult.getETag());
            result.setUrl(Constants.FILE_CLOUD_PATH + path + fileName);    
        }catch(Exception e){
            log.error("upload inputStream to oss error", e);
            result.setSuccess(false);
            result.setMsg("upload inputStream to oss error, message is " + e.getMessage());
        }
        return result;
    }
    
    @Override
    public OssUploadResult uploadImageToOssAsync(InputStream inputStream, String fileName, int fileSize) {
        return this.uploadImageToOss(inputStream, fileName, fileSize);
    }

    /**
     * 
     * @param file 文件
     * @param contextType 文件类型
     * @param path 路径
     * @param fileNameSuffix 文件名前缀
     * @return
     */
    private OssUploadResult uploadFileToOss(MultipartFile file,String contextType, String path,String fileName) {
        OssUploadResult result = new OssUploadResult();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(contextType);
        try{
//        	int[] widHei = FileSizeUtil.getImageWidthHeight(file.getInputStream());
            PutObjectResult pubResult = ossClient.putObject(OSS_BUCKET, path + fileName, file.getInputStream(),metadata);
            if(pubResult == null || StringUtils.isBlank(pubResult.getETag())){
                log.error("upload to oss error, put object result is null or etag is empty,  fileName {}", fileName);
                result.setSuccess(false);
                result.setMsg("upload to oss error, put object result is null or etag is empty");
                return result;
            }
            result.setSuccess(true);
            result.setMsg("upload to oss succeed");
            result.setFileMd5(pubResult.getETag());
            result.setUrl(Constants.FILE_CLOUD_PATH + path + fileName);
//            result.setWidth(widHei[0]);
//            result.setHeight(widHei[1]);
        }catch(Exception e){
            log.error("upload to oss error", e);
            result.setSuccess(false);
            result.setMsg("upload to oss error, message is " + e.getMessage());
        }
        return result;
    }
    
    private String getImageFileContentType(String fileName){
        if(StringUtils.isBlank(fileName) || fileName.indexOf(".") < 0){
            return "";
        }
        String fileSuffix = fileName.substring(fileName.lastIndexOf(".")+1);
        if("jpeg".equals(fileSuffix)||"jpg".equals(fileSuffix)){
            return "image/jpeg";
        }else if("png".equals(fileSuffix)){
            return "image/png";
        }else if("bmp".equals(fileSuffix)){
            return "image/bmp";
        }else if("apk".equals(fileSuffix)){
            return "application/vnd.android.package-archive";
        }else if ("pdf".equals(fileSuffix)){
            return "application/pdf";
        }
        return "";
    }
    
    public static void main(String[] args) {
        String fileName = "hello.jpeg";

        System.out.println(fileName.substring(fileName.lastIndexOf(".")+1));
    }


}
