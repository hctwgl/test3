package com.ald.fanbei;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfBoluomeDianyingDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJiayoukaDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJiudianDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeShoujiDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class Test {
	public static void main(String[] args) {
		// String salt = UserUtil.getSalt();
		// String password =
		// UserUtil.getPassword("c98954a476fbad043a0118d0a53318b8", salt);
		//
		// System.out.println("salt=" + salt + "$$$$$$$$$$$$$$password=" +
		// password);
		
//		getUserAccount();
//		try{
//			genFanbeiRedpackage("/Users/suweili/Desktop/90164");
//		}catch(Exception e){
//			System.out.println(e);
//		}

		//System.out.println(new Date(1508998500000L));
		
//		try {
//			BaseController.getTestUser(URLDecoder.decode("http://btestapp.51fanbei.com/fanbei-web/activity/doubleTwelve?groupId=http://btestapp.51fanbei.com/fanbei-web/activity/doubleTwelve?groupId=74&_appInfo=%7B%22id%22%3A%22a_867068024279399_1511925513970_www%22%2C%22time%22%3A%221511925513970%22%2C%22sign%22%3A%2226f66db053efb4d1c3ccb73ba56dc6e1d66e3c6fe3ef3bd376dab55284f0a579%22%2C%22userName%22%3A%2215669066271%22%2C%22netType%22%3A%22WIFI%22%2C%22appVersion%22%3A%22397%22%7D","utf-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	    
        	try {
        	    //,        \"coupon\": {            \"price\": 10,            \"title\": \"10元外卖劵\"        },\"couponId\": \"18879352\"
        	    //AfBoluomeShoujiDo afBoluomeShoujiDo = JSON.parseObject(" {        \"area\": \"北京\",        \"canCancel\": 0,        \"cancelledAt\": 1513056463818,        \"channel\": \"dhst\",        \"displayStatus\": \"已取消\",        \"flow\": \"50M\",        \"goodsId\": \"cucc_50M\", \"id\": \"ala102894012481600\",        \"isp\": \"联通\",        \"name\": \"(全国)北京联通50M - 186****5787\",        \"orderPrice\": 5.9,        \"orderType\": \"liuliang\",        \"partnerId\": \"\",        \"phone\": \"18612115787\",        \"price\": 5.9,        \"status\": 8,        \"userId\": \"1953217\",        \"userPhone\": \"13043324643\"    ,        \"coupon\": {            \"price\": 10,            \"title\": \"10元外卖劵\"        },\"couponId\": \"18879352\"}",AfBoluomeShoujiDo.class);
        	    //AfBoluomeJiayoukaDo afBoluomeShoujiDo = JSON.parseObject("{        \"canCancel\": 1,        \"cardId\": \"1000113200005437747\",        \"cardnum\": 1,        \"categoryId\": \"1\",        \"channel\": \"gaoyang\",        \"displayStatus\": \"待支付\",        \"facePrice\": \"100.0\",        \"gameid\": \"GAME60560\",        \"id\": \"ala002893302465069\",        \"isp\": \"中石化\",        \"name\": \"中石化卡:100****7747 充值 100.0元\",        \"orderPrice\": 100,        \"orderType\": \"jiayouka\",        \"orderTypeName\": \"加油卡\",        \"partnerId\": \"\",        \"phone\": \"18612115787\",        \"price\": 100,        \"productId\": \"1503233307\",        \"status\": 2,        \"userId\": \"980186\",        \"userName\": \"\",        \"userPhone\": \"18850843825\",        \"coupon\": {            \"price\": 10,            \"title\": \"10元外卖劵\"        },\"couponId\": \"18879352\"}",AfBoluomeJiayoukaDo.class);
        	    //AfBoluomeDianyingDo afBoluomeDianyingDo = JSON.parseObject("{        \"canCancel\": 1,        \"channel\": \"kou\",        \"cinema\": {            \"addr\": \"萧山区萧绍路636号金马同盛4楼\t\",            \"cityId\": \"119\",            \"id\": 6775,            \"lat\": \"30.164673160795733\",            \"lng\": \"120.28619972540322\",            \"name\": \"时代凤凰国际影城\"        },        \"count\": 2,        \"displayStatus\": \"待支付\",        \"endTime\": \"00:14\",        \"hallName\": \"2号厅\",        \"id\": \"ala102895611297858\",        \"language\": \"英语\",        \"movieName\": \"鲨海\",        \"moviePhoto\": \"https://audio.komovie.cn/sns/2017/11/20/39939629580152002902.jpg\",        \"name\": \"鲨海\",        \"orderPrice\": 48,        \"orderType\": \"dianying\",        \"partnerId\": \"a1513067761444078251\",        \"phone\": \"18612115787\",        \"price\": 48,        \"screenType\": \"2D\",        \"showDate\": \"2017-12-12\",        \"showTime\": \"22:45\",        \"status\": 2,        \"userId\": \"1953217\",        \"userPhone\": \"13043324643\"    }",AfBoluomeDianyingDo.class);
        	    AfBoluomeJiudianDo afBoluomeJiudianDo =JSON.parseObject(" {        \"ArrivalDate\": \"2017-12-12\",        \"Contact\": {            \"Mobile\": \"18612115787\",            \"Name\": \"Gaojibin\"        },        \"CreditCard\": {            \"ExpirationMonth\": \"\",            \"ExpirationYear\": \"\",            \"IdType\": \"IdentityCard\"        },        \"DepartureDate\": \"2017-12-13\",        \"EarliestArrivalTime\": \"2017-12-12 13:00\",        \"GuaranteeAmount\": 427,        \"GuaranteePayType\": 0,        \"GuaranteeTypeCode\": \"2\",        \"HotelAddr\": \"钱江世纪城金鸡路2380号\",        \"HotelId\": \"11538334\",        \"HotelImg\": \"https://dimg04.c-ctrip.com/images/200j0l000000d0nav571B_R_550_412.jpg\",        \"HotelName\": \"杭州奥体华美达安可酒店\",        \"HotelTel\": \"0571-8353877\",        \"IsGuarantee\": 0,        \"LatestArrivalTime\": \"2017-12-12 14:00\",        \"NumberOfCustomers\": 1,        \"NumberOfRooms\": 1,        \"OrderRooms\": [            {                \"Customers\": [                    {                        \"Name\": \"Gaojibin\"                    }                ]            }        ],        \"PaymentType\": \"Prepay\",        \"RatePlanCategory\": \"501\",        \"RatePlanId\": \"107727236\",        \"RoomInfo\": \"双床1.35米 全部房间Wi-Fi 有早餐\",        \"RoomName\": \"高级双床房(特惠)[双早]\",        \"RoomTypeId\": \"53724407\",        \"canCancel\": 1,        \"changeRule\": {            \"canChange\": false,            \"endTime\": \"2017-12-13T00:00:00\",            \"forfeit\": 427,            \"startTime\": \"2017-09-04T18:00:00\"        },        \"channel\": \"ctrip\",        \"cityId\": \"17\",        \"displayStatus\": \"待支付\",        \"id\": \"ala002893219103267\",        \"isOnlinePaid\": true,        \"isPrepay\": true,        \"lat\": \"30.237233\",        \"lng\": \"120.238887\",        \"name\": \"杭州奥体华美达安可酒店 - 高级双床房(特惠)[双早]\",        \"nightlyRates\": [            {                \"date\": \"2017-12-12\",                \"member\": 427            }        ],        \"orderPrice\": 427,        \"orderType\": \"jiudian\",        \"partnerId\": \"5082907134\",        \"price\": 427,        \"status\": 2,        \"userId\": \"980186\",        \"userPhone\": \"18850843825\"    }",AfBoluomeJiudianDo.class);
        	    
        	    System.out.println(afBoluomeJiudianDo.toString());
//        	    Map<String, String> params = new HashMap<String, String>();
//        	    params.put(BoluomeCore.ORDER_ID, "ala202895912378377");
//        	    params.put(BoluomeCore.TIME_STAMP, String.valueOf(new Date().getTime() / 1000));
//        
//        	    // String beforeSign = AesUtil.decrypt("6aSsbSMMhKBe8+bH5wmhBw==",
//        	    // "testC1b6x@6aH$2dlw") + BoluomeCore.concatParams(params) +
//        	    // AesUtil.decrypt("OjGQ+SJfDqChsV4l3XgfAXgwxh35sz2KgeMq/WAQvZPQroYLtEIaqFC8Jmft4E4B",
//        	    // "testC1b6x@6aH$2dlw");
//        	    String beforeSign = "7887978286" + BoluomeCore.concatParams(params) + "OMulI3N5ERyUko5fBKEs3UQzxamly2WC";
//        	    beforeSign = URLEncoder.encode(beforeSign, "utf-8").toUpperCase();
//        	    String sign = DigestUtil.MD5(beforeSign).toUpperCase();
//        
//        	    // params.put("appKey", AesUtil.decrypt("6aSsbSMMhKBe8+bH5wmhBw==",
//        	    // "testC1b6x@6aH$2dlw"));
//        	    params.put("appKey", "7887978286");
//        	    params.put(BoluomeCore.SIGN, sign);
//        	    String paramsStr = BoluomeCore.createLinkString(params);
//        
//        	    System.out.println(HttpUtil.doGet("https://api.otosaas.com/91ala/orders/v1/detail?" + paramsStr, 100));
        	} catch (Exception e) {
        	    // TODO Auto-generated catch block
        	    e.printStackTrace();
        	}
		
	}
	
	
	
	public static void genFanbeiRedpackage(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("/Users/suweili/Desktop/output.txt"), true)));
			
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			//特别注意替换  红包id,红包名称，红包金额，红包有效时间
			String sql = "insert into `af_user_coupon`(`gmt_create`,`gmt_modified`,`user_id`,`coupon_id`,`gmt_start`,`gmt_end`,`status`,`gmt_use`,`source_type`,`source_ref`)"
					+ " values(NOW(),NOW(),#userId#,'39','2017-04-19 00:00:00','2017-05-19 23:59:59','NOUSE',null,'REGIST','SYS');\n";
			while ((tempString = reader.readLine()) != null) {
				String temp = sql.replace("#userId#", tempString);
				out.append(temp);
				System.out.println(temp);
			}
			reader.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}
	
	@SuppressWarnings("unused")
	private static Integer getUserAccount(){
		Connection conn = getConn();
		String sql = "SELECT password ,id  from  af_user_account where  `password` != ''";
		PreparedStatement pstmt;

		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			int col = rs.getMetaData().getColumnCount();
			System.out.println("============================");
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			while (rs.next()) {

				
				Map<String, Object> data = new HashMap<String, Object>();
				
				for (int i = 1; i <= col; i++) {
					String columnLabel = rs.getMetaData().getColumnLabel(i);
					
					String tem = rs.getString(i) == null ? "" : rs.getString(i);
					
					
					switch (columnLabel) {
					case "id":
						Long id = NumberUtil.objToLongDefault(tem, 0L);
					
						data.put("id", id);

						break;
					case "password":
						String salt = UserUtil.getSalt();
						String password = UserUtil.getPassword(tem, salt);
						data.put("password", password);
						data.put("salt", salt);

						break;
				

					default:
						break;
					}
					
					
				}
				
				list.add(data);
			}
			
			
			 String sql2="";
				for (Map<String, Object> temDate : list) {
					
					String salt= temDate.get("salt").toString();
					String id= temDate.get("id").toString();
					String password = temDate.get("password").toString();

					 sql2 =sql2+ "update 51fanbei_app.af_user_account set  `salt`='"+salt+"', `password`='"+password+"' where salt='' and `id`="+id+";"
					 		+ "";

			            
				}
				
				String textSql = "/Users/suweili/Desktop/sql"+1+".txt";
				
				 writeTxtFile(sql2,new File(textSql));
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
			return 1;
	}
	
	
	@SuppressWarnings("unused")
	private static Integer getUserInfoPage(){
		Connection conn = getConn();
		String sql = "SELECT COUNT(id) total FROM 51fanbei_app.af_user";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			
			ResultSet rs = pstmt.executeQuery();
			int col = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= col; i++) {
					String columnLabel = rs.getMetaData().getColumnLabel(i);
					if(StringUtils.equals("total", columnLabel)){

						Integer total = NumberUtil.objToIntDefault(rs.getString(i) == null ? "" : rs.getString(i), 1);
						return total/500;
					}

				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		return 1;
	}
	
//获取分页用户信息
	@SuppressWarnings("unused")
	private static Integer afUserInfo(int page) throws SQLException {
		int pageCount = 80000;
		Connection conn = getConn();
		
		int start = (page - 1) * pageCount;
		int end =  (page * pageCount-1);
		String sql = "SELECT id, password ,salt, user_name  from  51fanbei_app.af_user ORDER BY id asc LIMIT "
				+ start + "," +end;
		PreparedStatement pstmt;
		System.out.println("start="+start+"end ="+end);
		 SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		 System.out.println(df1.format(new Date()));// new Date()为获取当前系统时间
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			int col = rs.getMetaData().getColumnCount();
			System.out.println("============================");
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			while (rs.next()) {

				
				Map<String, Object> data = new HashMap<String, Object>();
				
				for (int i = 1; i <= col; i++) {
					String columnLabel = rs.getMetaData().getColumnLabel(i);
					
					String tem = rs.getString(i) == null ? "" : rs.getString(i);
					
					
					switch (columnLabel) {
					case "id":
						Long id = NumberUtil.objToLongDefault(tem, 0L);
						Long invteLong = Constants.INVITE_START_VALUE + id;
						// TODO 优化邀请码规则
						String inviteCode = Long.toString(invteLong, 36);
						data.put("recommend_code", inviteCode);
						data.put("id", id);

						break;
					case "password":
						String salt = UserUtil.getSalt();
						String password = UserUtil.getPassword(tem, salt);
						data.put("password", password);
						data.put("salt", salt);

						break;
				

					default:
						break;
					}
					
					
				}
				
				list.add(data);

			}
//			conn.setAutoCommit(false);
//			PreparedStatement pst = (PreparedStatement) conn.prepareStatement(sql);
			 String sql2="";
			for (Map<String, Object> temDate : list) {
				
				String recommend_code = temDate.get("recommend_code").toString();
				String salt= temDate.get("salt").toString();
				String id= temDate.get("id").toString();
				String password = temDate.get("password").toString();

				 sql2 =sql2+ "update `51fanbei_app`.`af_user` set  `salt`='"+salt+"',`recommend_code`='"+recommend_code+"', `password`='"+password+"' where salt='' and `id`="+id+";"
				 		+ "";

		            
			}
			String textSql = "/Users/suweili/Desktop/sql"+page+".txt";
			
			 writeTxtFile(sql2,new File(textSql));

			 SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			 System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
			System.out.println("============================"+page);
			return 1;

		} catch (SQLException e) {
//			conn.rollback();// 回滚JDBC事务

			e.printStackTrace();
		}
		return -1;

	}

	private static Connection getConn() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://192.168.101.61:3308/51fanbei_app?useUnicode=true&characterEncoding=UTF-8";
		String username = "root";
		String password = "edushitest123";

		Connection conn = null;
		try {
			Class.forName(driver); // classLoader,加载对应驱动
			conn = (Connection) DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	
	
	 public static void writeTxtFile(String content,File  file){  
//		 File file = new File("c:/newfile.txt");
//		  String content = "This is the text content";

		  try (FileOutputStream fop = new FileOutputStream(file)) {

		   // if file doesn't exists, then create it
		   if (!file.exists()) {
		    file.createNewFile();
		   }

		   // get the content in bytes
		   byte[] contentInBytes = content.getBytes();

		   fop.write(contentInBytes);
		   fop.flush();
		   fop.close();

		   System.out.println("Done");

		  } catch (IOException e) {
		   e.printStackTrace();
		  }
	 }
	 
}
