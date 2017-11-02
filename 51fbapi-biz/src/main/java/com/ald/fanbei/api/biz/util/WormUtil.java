package com.ald.fanbei.api.biz.util;

import com.ald.fanbei.api.biz.bo.worm.RateBo;
import com.ald.fanbei.api.biz.bo.worm.RateDetailBo;
import com.ald.fanbei.api.common.util.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName WormUtil.
 * @desc
 * 爬虫工具类
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/19 16:35
 */
@Component("wormUtil")
public class WormUtil {

    private static final Logger logger = LoggerFactory.getLogger(WormUtil.class);

    private static final String WORM_USERAGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:0.9.4)";
    private static final String WORM_REFERER = "Referer";

    private static Map<String,String> parseHtml(String html) {
        Map<String,String> map = new HashMap<>();
        
        Document document = Jsoup.parse(html);
        Elements elements = document.
                select("script");
        String sellerId= "";
        for(Element element : elements) {
        	String elementStr = element.data();
        	if(elementStr.contains("sellerId")){
        		int index = elementStr.indexOf("sellerId");
        		map.put("sellerId",elementStr.substring(index,elementStr.length()).split(":")[1].split(",")[0].replace("\"", ""));
        		break;
        	}
        }
        return map;
    }

    private static String getInitParams(String url){
        HttpClientBuilder builder = HttpClients.custom();
        builder.setUserAgent(WORM_USERAGENT);
        CloseableHttpClient httpClient = builder.build();
        final HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(WORM_REFERER, url);
        httpGet.getParams().setParameter("http.protocol.allow-circular-redirects", true);
        CloseableHttpResponse response = null;
        String result = null;
        try {
            response = httpClient.execute(httpGet);
            final HttpEntity entity = response.getEntity();

            if (entity != null) {
                result = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
            response.close();
            httpClient.close();
        } catch (IOException e) {
            logger.error("WormUtil.getInitParams error",e);
        }
        return result;
    }

    /**
     * 获取评论内容
     * @param numId
     * @param source
     * @param page
     * @return
     * @throws IOException
     */
    public static List<RateBo> getRateInfo(String numId, String source,String page) throws IOException {
    	List<RateBo> rateBoList = new ArrayList<>();
    	
    	HttpClientBuilder builder = HttpClients.custom();
        builder.setUserAgent(WORM_USERAGENT);
        CloseableHttpClient httpClient = builder.build();
        HttpGet httpGet = null;
        CloseableHttpResponse response = null;

        if (StringUtils.equals("TAMLL", source)) {
            String html = WormUtil.getInitParams("https://detail.tmall.com/item.htm?id="+numId);
            Map<String,String> map = WormUtil.parseHtml(html);
            String sellerId = map.get("sellerId");

            String url = "https://rate.tmall.com/list_detail_rate.htm?itemId="+numId+"&sellerId="+sellerId+"&order=3&pageSize=10&currentPage="+page;
            httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            final HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
            result = result.substring(result.indexOf("{"), result.length());
            JSONObject object = (JSONObject) JSONObject.parse(result);
            JSONArray rateList = object.getJSONArray("rateList");

            for (int i = 0; i < rateList.size(); i++) {
            	JSONObject value = (JSONObject)rateList.get(i);
            	
            	RateBo rateBo = new RateBo();
            	rateBo.setContent(value.getString("rateContent"));
            	rateBo.setAuctionSku(value.getString("auctionSku"));
            	rateBo.setRateTime(value.getString("rateDate"));
            	rateBo.setReply(value.getString("reply"));
            	String pics = value.getString("pics");
            	if(StringUtils.isBlank(pics)){
            		rateBo.setType("0");
            	}else{
            		rateBo.setType("1");
            		String[] picsNum = pics.split(",");
            		List<RateDetailBo> rateDetailBoList = new ArrayList<>();
            		for(String pic : picsNum){
            			RateDetailBo rateDetailBo = new RateDetailBo();
            			rateDetailBo.setPicUrl("http:"+pic.replace("[\"", "").replace("]\"", ""));
            			rateDetailBoList.add(rateDetailBo);
            		}
            		rateBo.setRateDetail(rateDetailBoList);
            	}
            	rateBo.setUserName(value.getString("displayUserNick"));
            	
            	rateBoList.add(rateBo);
			}            
            response.close();
            httpClient.close();
            return rateBoList;
        }
        if(StringUtils.equals("TAOBAO", source)){
            String html = WormUtil.getInitParams("https://item.taobao.com/item.htm?id="+numId);
            Map<String,String> map = WormUtil.parseHtml(html);
            String sellerId = map.get("sellerId");

        	String url = "https://rate.taobao.com/feedRateList.htm?auctionNumId="+numId+"&userNumId="+sellerId.replace("'","").trim()+"&currentPageNum="+page+"&pageSize=10";
            httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            final HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
            result = result.replace("(","").replace(")","");
            JSONObject object = (JSONObject) JSONObject.parse(result);
            JSONArray rateList = object.getJSONArray("comments");

            for (int i = 0; i < rateList.size(); i++) {
                JSONObject value = (JSONObject)rateList.get(i);

                RateBo rateBo = new RateBo();
                rateBo.setContent(value.getString("content"));

                JSONObject auction = (JSONObject) JSONObject.parse(value.getString("auction"));
                rateBo.setAuctionSku(auction.getString("sku"));

                JSONObject user = (JSONObject) JSONObject.parse(value.getString("user"));
                rateBo.setUserName(user.getString("nick"));

                String date = value.getString("date");
                if(StringUtils.isBlank(date)){
                    rateBo.setRateTime(DateUtil.formatDate(new Date()));
                }else{
                    rateBo.setRateTime(DateUtil.formatDate(DateUtil.parseDate(date,"yyyy年MM月dd日 HH:mm"),"yyyy-MM-dd HH:mm:ss"));
                }


                JSONArray photos = JSONObject.parseArray(value.getString("photos"));
                if(null==photos || photos.size() ==0){
                    rateBo.setType("0");
                }else{
                    rateBo.setType("1");
                    List<RateDetailBo> rateDetailBoList = new ArrayList<>();
                    for (int j = 0; j < photos.size(); j++) {
                        JSONObject photosObject = (JSONObject)photos.get(j);
                        String pic = photosObject.getString("thumbnail");
                        RateDetailBo rateDetailBo = new RateDetailBo();
                        rateDetailBo.setPicUrl("http:"+pic.replace("[\"", "").replace("]\"", ""));
                        rateDetailBoList.add(rateDetailBo);
                    }
                    rateBo.setRateDetail(rateDetailBoList);
                }
                rateBo.setReply(value.getString("reply"));
                rateBoList.add(rateBo);
            }
            response.close();
            httpClient.close();
        }
        
        if (StringUtils.equals("JD", source)) {
            page = String.valueOf(Integer.parseInt(page) - 1);
            String url = "https://sclub.jd.com/comment/productPageComments.action?productId="+numId+"&score=0&sortType=3&page="+page+"&pageSize=10&callback=fetchJSON_comment98vv37464";
            httpGet = new HttpGet(url);
            response = httpClient.execute(httpGet);
            final HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
            result = result.substring(result.indexOf("{"), result.length()-2);
            JSONObject object = (JSONObject) JSONObject.parse(result);
            JSONArray rateList = object.getJSONArray("comments");
            for (int i = 0; i < rateList.size(); i++) {
                JSONObject value = (JSONObject)rateList.get(i);
                RateBo rateBo = new RateBo();
                rateBo.setContent(value.getString("content"));
                rateBo.setAuctionSku(value.getString("productColor")+"&nbsp;&nbsp;"+value.getString("productSize"));
                rateBo.setUserName(value.getString("nickname"));
                rateBo.setRateTime(value.getString("creationTime"));

                JSONArray photos = JSONObject.parseArray(value.getString("images"));
                if(null==photos || photos.size() ==0){
                    rateBo.setType("0");
                }else{
                    rateBo.setType("1");
                    List<RateDetailBo> rateDetailBoList = new ArrayList<>();
                    for (int j = 0; j < photos.size(); j++) {
                        JSONObject photosObject = (JSONObject)photos.get(j);
                        String pic = photosObject.getString("imgUrl");
                        RateDetailBo rateDetailBo = new RateDetailBo();
                        rateDetailBo.setPicUrl("http:"+pic.replace("[\"", "").replace("]\"", ""));
                        rateDetailBoList.add(rateDetailBo);
                    }
                    rateBo.setRateDetail(rateDetailBoList);
                }
                rateBo.setReply("");
                rateBoList.add(rateBo);
            }
            response.close();
            httpClient.close();
        }
        return rateBoList;
    }

    public static void main(String[] args) throws IOException {
        List<RateBo> rateBo = getRateInfo("554968243002","TAMLL","1");
    }
}
