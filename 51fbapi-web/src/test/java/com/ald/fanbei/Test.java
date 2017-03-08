package com.ald.fanbei;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class Test {
	public static void main(String[] args) {
//		String salt = UserUtil.getSalt();
//		String password = UserUtil.getPassword("c98954a476fbad043a0118d0a53318b8", salt);
//
//		System.out.println("salt=" + salt + "$$$$$$$$$$$$$$password=" + password);
		
		System.out.println("============================");
		Connection conn = getConn();

//		try {
//			afUserInfo(1L);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	private static Integer afUserInfo(Long page) throws SQLException {
		Long pageCount = 100L;
		Connection conn = getConn();
		String sql = "SELECT u.id,FROM_UNIXTIME(u.reg_time, '%Y-%m-%d %H:%i:%S') gmt_create,FROM_UNIXTIME(login_time, '%Y-%m-%d %H:%i:%S') gmt_modified,u.phone user_name,u.passwordCASE sex WHEN 0 THEN 'F' WHEN 1 THEN 'M' ELSE 'U' END u.gender,u.nickname nick,(CASE WHEN u.head_img IS NULL THEN '' ELSE u.head_img END) AS avatar,(CASE WHEN u.realname IS NULL THEN '' ELSE u.realname END) real_name,u.phone mobile,(CASE WHEN u.email IS NULL THEN '' ELSE u.email END) email,(CASE WHEN u.birth IS NULL THEN '' ELSE u.birth END) birthday,a1.ProvinceName  province, a2.CityName city,(CASE WHEN u.address IS NULL THEN '' ELSE u.address END)AS address,u.extend_id recommend_id FROM fnuo_lighteshopbe_user  u LEFT JOIN  fnuo_lighteshopbe_city  a2 ON a2.CityID = u.city_id	LEFT JOIN 	fnuo_lighteshopbe_province  a1 ON a1.ProvinceID =  a2.ProvinceID ORDER BY u.id asc  LIMIT "+(page-1)*pageCount+","+page*pageCount;
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			int col = rs.getMetaData().getColumnCount();
			System.out.println("============================");
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> data = new HashMap<String, Object>();
				List<Object> priceList = new ArrayList<Object>();

				for (int i = 1; i <= col; i++) {
					String columnLabel = rs.getMetaData().getColumnLabel(i);
					
					
					System.out.print(rs.getString(i) + "\t");
					if ((i == 2) && (rs.getString(i).length() < 8)) {
						System.out.print("\t");
					}
					
				}
				
				System.out.println("============================");

				
			}
			
			return 1;

		} catch (SQLException e) {
			conn.rollback();// 回滚JDBC事务
			e.printStackTrace();
		}
		return -1;
		
	}
	
	
	
	  
	  
	private static Connection getConn() {
		String driver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://192.168.101.61:3308/www.91ala.com?useUnicode=true&characterEncoding=UTF-8";
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
}
