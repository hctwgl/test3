package com.ald.fanbei;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.alibaba.druid.util.StringUtils;
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
		try {
			afUserInfo(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
