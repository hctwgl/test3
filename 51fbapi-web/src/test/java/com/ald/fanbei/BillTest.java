package com.ald.fanbei;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

/**
 * 未出账账单表
 * @author hx
 *
 */
public class BillTest {

	private static int getPageCount(int pageSize){
		Connection conn = getConn();
		String sql = "SELECT  count(1) from  af_borrow where is_delete=0 and type='CONSUME' and status='TRANSED'";
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {  
				return (int)Math.ceil(rs.getInt(1)/pageSize);
            }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
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
	
	public static void main(String[] args) {
		int pageSize = 100;
		int pageCount = getPageCount(pageSize);
		for (int i = 0; i <pageCount; i++) {
			int begin = pageSize*i;
			String sql = "SELECT  id,nper,nper_amount,borrow_no,user_id,gmt_create,name,amount from  af_borrow where is_delete=0 and type='CONSUME' and status='TRANSED' limit ?,?";
			PreparedStatement pstmt =null;
			PreparedStatement pstmt1 =null;
			PreparedStatement pstmt2 =null;
			Connection conn = getConn();
			try {
				pstmt = (PreparedStatement) conn.prepareStatement(sql);
				pstmt.setInt(1,begin);
				pstmt.setInt(2,pageSize);
				ResultSet rs = pstmt.executeQuery();
				System.out.println(pstmt.asSql());
				while (rs.next()) {  
					conn.setAutoCommit(false);
					int nper = rs.getInt(2);
					Long borrowId = rs.getLong(1);
					BigDecimal nperAmount = rs.getBigDecimal(3);
					String borrowNo = rs.getString(4);
					Long userId = rs.getLong(5);
					Date gmtCreate = rs.getDate(6);
					String name = rs.getString(7);
					BigDecimal boAmount = rs.getBigDecimal(8);
					int billYear = 0,billMonth =0;
					BigDecimal principleAmount = BigDecimal.ZERO;
					BigDecimal interestAmount =  BigDecimal.ZERO;
					BigDecimal poundageAmount =  BigDecimal.ZERO;

					for (int j = 1; j <= nper; j++) {
						String sql1 = "SELECT  bill_year,bill_month,principle_amount,interest_amount,poundage_amount from  af_borrow_bill where is_delete=0 and borrow_id=? and bill_nper=?";
						pstmt1 = (PreparedStatement) conn.prepareStatement(sql1);
						pstmt1.setLong(1, borrowId);
						pstmt1.setInt(2,j);
						ResultSet rs1 = pstmt1.executeQuery();
						if(rs1.next()){
							billYear = rs1.getInt(1);
							billMonth = rs1.getInt(2);
							principleAmount = rs1.getBigDecimal(3);
							interestAmount = rs1.getBigDecimal(4);
							poundageAmount = rs1.getBigDecimal(5);
						}else{
							if(billYear==0){
								//获取账单日
								Date startDate = gmtCreate;
								Date billDate = (Date) DateUtil.addDays(DateUtil.getStartOfDate(DateUtil.getFirstOfMonth(startDate)), 9);
								if(billDate.after(startDate)){//账单日之后，当前月
									
								}else{//账单月-1
									startDate = (Date) DateUtil.addMonths(startDate, -1);
								}
								String[] billDay = DateUtil.formatDate(startDate, DateUtil.MONTH_PATTERN).split("-");
								if(billDay.length==2){
									billYear = NumberUtil.objToIntDefault(billDay[0], 0);
									billMonth = NumberUtil.objToIntDefault(billDay[1], 0);
								}
								interestAmount = boAmount.multiply(new BigDecimal(0.18)).divide(new BigDecimal(12),2,BigDecimal.ROUND_HALF_UP);
								BigDecimal perdageAmount = boAmount.multiply(new BigDecimal(0.006));
								if(perdageAmount.compareTo(new BigDecimal(10))==-1){
									poundageAmount = new BigDecimal(10).divide(new BigDecimal(nper),2,BigDecimal.ROUND_HALF_UP);
								}else if(perdageAmount.compareTo(new BigDecimal(50))>0){
									poundageAmount = new BigDecimal(50).divide(new BigDecimal(nper),2,BigDecimal.ROUND_HALF_UP);
								}else{
									poundageAmount = perdageAmount.divide(new BigDecimal(nper),2,BigDecimal.ROUND_HALF_UP);
								}
								principleAmount = nperAmount.subtract(interestAmount).subtract(poundageAmount);
							}else{
								if(billMonth==12){
									billYear++;
									billMonth = 1;
								}else{
									billMonth++;
								}
							}
							//插入数据
							String sql2 = "insert into af_borrow_bill(gmt_create,gmt_modified,user_id,borrow_id,borrow_no,NAME,TYPE,gmt_borrow,bill_year,bill_month,nper,bill_nper,"+ 
										"bill_amount,STATUS,overdue_status,principle_amount,interest_amount,poundage_amount) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
							pstmt2 = (PreparedStatement) conn.prepareStatement(sql2);
							pstmt2.setDate(1, gmtCreate);
							pstmt2.setDate(2, gmtCreate);
							pstmt2.setLong(3, userId);
							pstmt2.setLong(4, borrowId);
							pstmt2.setString(5, borrowNo);
							pstmt2.setString(6, name);
							pstmt2.setString(7, "CONSUME");
							pstmt2.setDate(8, gmtCreate);
							pstmt2.setInt(9,billYear);
							pstmt2.setInt(10,billMonth);
							pstmt2.setInt(11, nper);
							pstmt2.setInt(12, j);
							pstmt2.setBigDecimal(13, nperAmount);
							pstmt2.setString(14, "N");
							pstmt2.setString(15, "N");
							pstmt2.setBigDecimal(16, principleAmount);//本金=每期
							pstmt2.setBigDecimal(17, interestAmount);
							pstmt2.setBigDecimal(18, poundageAmount);
							pstmt2.executeUpdate();
						}
					}
	            }
				conn.commit();
				System.out.println("第"+i+"次提交");
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
