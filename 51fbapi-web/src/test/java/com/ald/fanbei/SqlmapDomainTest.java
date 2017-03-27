package com.ald.fanbei;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 针对创建新表，需要些sqlmap,domain时可以使用该类减少人肉操作
 * 
 * 需要修改url,username,password,tableName即可
 * 
 * 需要依赖
        <dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.26</version>
		</dependency>
 * 类SqlmapDomainTest.java的实现描述：SqlmapDomainTest
 * @author jinhu.chenjh 2015年5月27日 上午11:38:42
 */
public class SqlmapDomainTest {
	
    String url = "jdbc:mysql://192.168.101.85:3306/51fanbei_app" ;    
    String username = "51fanbei" ;   
    String password = "Hello1234" ;   
    private static String tableName = "af_shop";
    private static String beanName = "";
    private static int tableNamePreLen = 2;
	Connection con = null;
	
	public static void main(String[] args) throws Exception {
		
		SqlmapDomainTest test = new SqlmapDomainTest();
		Map<String,String> fieldsTypeMap = test.getColumnNameTypeMap(tableName);
		
		//domain属性
		printDomainPro(fieldsTypeMap);
		//ibatis sqlmap add
		printInsertParam(fieldsTypeMap);
		//ibatis sqlmap update
		printUpdateCondition(fieldsTypeMap);
		//ibatis sqlmap where condition
		printWhereCondition(fieldsTypeMap);
		//设置值 单元测试造数据使用
//		printSetData(fieldsTypeMap);
	}
	
	public void initDataSource() throws Exception{
		Class.forName("com.mysql.jdbc.Driver") ;  
		con = DriverManager.getConnection(url , username , password ) ;    
	}
	
	/**
	 * 
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public Map<String,String> getColumnNameTypeMap(String tableName) throws Exception {
		initDataSource();
		Statement stmt =  con.createStatement() ; 
		ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName) ;
		ResultSetMetaData rsmd = rs.getMetaData();
		Map<String,String> columnMap = new LinkedHashMap<String,String>();
		int columns = rsmd.getColumnCount();
		for(int i = 1 ; i <= columns ; i ++){
//			System.out.println("------->" + rsmd.getColumnName(i));
			columnMap.put(rsmd.getColumnName(i), rsmd.getColumnClassName(i));
		}
		
		return columnMap;
	}
	
	private static void printDomainPro(Map<String,String> fieldType){
	    beanName = tableName.substring(0,1).toUpperCase() + tableName.substring(1);
	    while(beanName.indexOf("_") >=0){
	        int firstSpe = beanName.indexOf("_");
	        beanName = beanName.substring(0,firstSpe) + beanName.substring(firstSpe+1,firstSpe+2).toUpperCase() + beanName.substring(firstSpe+2);
	    }
	    System.out.println(beanName);
	    System.out.println("   /**");
	    System.out.println("    * 增加记录");
	    System.out.println("    * @param " + beanName.substring(0,1).toLowerCase() + beanName.substring(1) + "Do");
	    System.out.println("    * @return");
	    System.out.println("    */");
	    System.out.println("    int add" + beanName.substring(tableNamePreLen) + "(" + beanName + "Do " + beanName.substring(0,1).toLowerCase() + beanName.substring(1) + "Do);");


	    System.out.println("   /**");
	    System.out.println("    * 更新记录");
	    System.out.println("    * @param " + beanName.substring(0,1).toLowerCase() + beanName.substring(1) + "Do");
	    System.out.println("    * @return");
	    System.out.println("    */");
	    System.out.println("    int update" + beanName.substring(tableNamePreLen) + "(" + beanName + "Do " + beanName.substring(0,1).toLowerCase() + beanName.substring(1) + "Do);");
	    
	    
		System.out.println("===============================domain start==================================");
		String columns = "";
		for(String item:fieldType.keySet()){
			String itemSource = item;
			item = item.toLowerCase();
	         if("is_delete".equals(item)){
	                continue;
	            }
	         columns = columns + "," + item;
			if(item.indexOf("_") >= 0){
				String head = item.substring(0, item.indexOf("_"));
				String mid = item.substring(item.indexOf("_")+1,item.indexOf("_")+2);
				String tail = item.substring(item.indexOf("_")+2);
				if(tail.indexOf("_")>=0){
					String head1 = tail.substring(0, tail.indexOf("_"));
					String mid1 = tail.substring(tail.indexOf("_")+1,tail.indexOf("_")+2);
					mid1 = mid1.toUpperCase();
					String tail1 = tail.substring(tail.indexOf("_")+2);
					tail = head1 + mid1 + tail1;
				}
				System.out.println("private " + getType(fieldType.get(itemSource)) + " " + head + mid.toUpperCase() + tail  + ";");
			}else{
				System.out.println("private " + getType(fieldType.get(itemSource)) + " " + item + ";");
			}
		}
		System.out.println("===============================domain end==================================\n\n");
		System.out.println(columns.substring(1));
		System.out.println("\n\n");
	}
	
	private static String getType(String type){
		if(type.indexOf("BigInteger") > 0){
			return "Long";
		}else if(type.indexOf("Timestamp") > 0){
			return "Date";
		}else if(type.indexOf("String") > 0){
			return "String";
		}else if(type.indexOf("Float") > 0){
			return "Float";
		}else if(type.indexOf("Integer") > 0){
			return "Integer";
		}else if(type.indexOf("Long") > 0){
			return "Long";
		}else if(type.indexOf("Boolean") > 0){
			return "Integer";
		}else if(type.indexOf("BigDecimal") >0){
			return "BigDecimal";
		}
		return "String";
	}
	
	private static void printInsertParam(Map<String,String> field){
		System.out.println("===============================ibatis insert start==================================");
		
		System.out.println("	<insert id=\"add" + beanName.substring(tableNamePreLen) + "\" parameterType=\"" + beanName+"Do\" useGeneratedKeys=\"true\" keyProperty=\"rid\">");
		String insertStr = "		INSERT INTO " + tableName + "(";
		for(String item:field.keySet()){
		    if("id".equals(item) || "is_delete".equals(item)){
		        continue;
		    }
		    insertStr = insertStr + item + " , ";
		}
		insertStr = insertStr.substring(0,insertStr.length() -3);
		insertStr = insertStr + ")";
		System.out.println(insertStr);
		System.out.println("		VALUES (");
		
		int currCount = 0;
		for(String item:field.keySet()){
			currCount ++;
            item = item.trim();
            if("id".equals(item) || "is_delete".equals(item)){
                continue;
            }
            if(item.equals("gmt_create") || item.equals("gmt_modified")){
            	System.out.println("    		NOW(),");
            	continue;
            }
            if(item.indexOf("_") >= 0){
                String head = item.substring(0, item.indexOf("_"));
                String mid = item.substring(item.indexOf("_")+1,item.indexOf("_")+2);
                String tail = item.substring(item.indexOf("_")+2);
                if(tail.indexOf("_")>=0){
                    String head1 = tail.substring(0, tail.indexOf("_"));
                    String mid1 = tail.substring(tail.indexOf("_")+1,tail.indexOf("_")+2);
                    mid1 = mid1.toUpperCase();
                    String tail1 = tail.substring(tail.indexOf("_")+2);
                    tail = head1 + mid1 + tail1;
                }
                if(currCount == field.size()){
                	System.out.println("    		#{" + head + mid.toUpperCase() + tail  + "}");
                }else{
                	System.out.println("    		#{" + head + mid.toUpperCase() + tail  + "},");
                }
                
            }else{
                System.out.println("    		#{" + item + "},");
            }
		}
		System.out.println("		)");
		System.out.println("	</insert>");
		System.out.println("===============================ibatis insert end==================================\n\n");
	}
	
	private static void printUpdateCondition(Map<String,String> field){
		System.out.println("===============================ibatis update condition start==================================");
		System.out.println("	<update id=\"update" + beanName.substring(tableNamePreLen) + "\" parameterType=\"" + beanName+"Do\" >");
		System.out.println("		UPDATE " + tableName + " SET gmt_modified = NOW()");
		for(String item:field.keySet()){
			if(item.trim().equals("id") || item.trim().equals("gmt_create") || item.trim().equals("gmt_modified")){
				continue;
			}
			item = item.trim().toLowerCase();
            if(item.indexOf("_") >= 0){
                String head = item.substring(0, item.indexOf("_"));
                String mid = item.substring(item.indexOf("_")+1,item.indexOf("_")+2);
                String tail = item.substring(item.indexOf("_")+2);
                if(tail.indexOf("_")>=0){
                    String head1 = tail.substring(0, tail.indexOf("_"));
                    String mid1 = tail.substring(tail.indexOf("_")+1,tail.indexOf("_")+2);
                    mid1 = mid1.toUpperCase();
                    String tail1 = tail.substring(tail.indexOf("_")+2);
                    tail = head1 + mid1 + tail1;
                }
                String prot = head + mid.toUpperCase() + tail;
                System.out.println("			<if test=\"" +  prot + " != null\">");
                System.out.println("    			," + item + "=#{" + prot + "}");
                System.out.println("			</if>");
            }else{
                System.out.println("			<if test=\"" +  item + " != null\">");
                System.out.println("    			," + item + "=#{" + item + "}");
                System.out.println("			</if>");
            }
		}
		System.out.println("		WHERE id = #{rid}");
		System.out.println("	</update>");
		System.out.println("===============================ibatis update condition end==================================\n");
		
		

		System.out.println("===============================ibatis delete sql start==================================");
		System.out.println("	<update id=\"delete" + beanName.substring(tableNamePreLen) + "\" parameterType=\"java.lang.Long\" >");
			System.out.println("		UPDATE "+tableName+" SET is_delete = 1 WHERE id=#{rid}");
		System.out.println("	</update>");
		System.out.println("===============================ibatis delete sql end  ==================================\n\n");
		
	}
	
	private static void printWhereCondition(Map<String,String> field){
		System.out.println("===============================iabatis where condition start==================================");
		for(String item:field.keySet()){
			item = item.trim().toLowerCase();
            if(item.indexOf("_") >= 0){
                String head = item.substring(0, item.indexOf("_"));
                String mid = item.substring(item.indexOf("_")+1,item.indexOf("_")+2);
                String tail = item.substring(item.indexOf("_")+2);
                if(tail.indexOf("_")>=0){
                    String head1 = tail.substring(0, tail.indexOf("_"));
                    String mid1 = tail.substring(tail.indexOf("_")+1,tail.indexOf("_")+2);
                    mid1 = mid1.toUpperCase();
                    String tail1 = tail.substring(tail.indexOf("_")+2);
                    tail = head1 + mid1 + tail1;
                }
                String prot = head + mid.toUpperCase() + tail;
                System.out.println("<if test=\"" +  prot + " != null\">");
                System.out.println("    AND " + item + "=#{" + prot + "}");
                System.out.println("</if>");
            }else{
                System.out.println("<if  test=\"" +  item + " != null\">");
                System.out.println("    AND " + item + "=#{" + item + "}");
                System.out.println("</if>");
            }
		}
		System.out.println("===============================ibatis where condition end==================================\n\n");
	}
	
//	private static void printResultMap(Map<String,String> field){
//		System.out.println("===============================ibatis result map start==================================");
//		for(String item:field.keySet()){
//			item = item.trim().toLowerCase();
//			if(item.indexOf("_") >= 0){
//				String head = item.substring(0, item.indexOf("_"));
//				String mid = item.substring(item.indexOf("_")+1,item.indexOf("_")+2);
//				String tail = item.substring(item.indexOf("_")+2);
//				if(tail.indexOf("_")>=0){
//					String head1 = tail.substring(0, tail.indexOf("_"));
//					String mid1 = tail.substring(tail.indexOf("_")+1,tail.indexOf("_")+2);
//					mid1 = mid1.toUpperCase();
//					String tail1 = tail.substring(tail.indexOf("_")+2);
//					tail = head1 + mid1 + tail1;
//				}
//				String prot = head + mid.toUpperCase() + tail;
//				System.out.println("<result property=\"" +  prot + "\" column=\"" + item + "\"/>");
//			}else{
//				System.out.println("<result property=\"" +  item + "\" column=\"" + item + "\"/>");
//			}
//		}	
//		System.out.println("===============================ibatis result map end==================================\n\n");
//	}
	
	@SuppressWarnings("unused")
	private static void printSetData(Map<String,String> field){
		for(String item:field.keySet()){
			item = item.trim().toLowerCase();
			if(item.indexOf("_") >= 0){
				String head = item.substring(0, item.indexOf("_"));
				String mid = item.substring(item.indexOf("_")+1,item.indexOf("_")+2);
				String tail = item.substring(item.indexOf("_")+2);
				if(tail.indexOf("_")>=0){
					String head1 = tail.substring(0, tail.indexOf("_"));
					String mid1 = tail.substring(tail.indexOf("_")+1,tail.indexOf("_")+2);
					mid1 = mid1.toUpperCase();
					String tail1 = tail.substring(tail.indexOf("_")+2);
					tail = head1 + mid1 + tail1;
				}
				System.out.println("item.set" + head.substring(0, 1).toUpperCase() + head.substring(1) + mid.toUpperCase() + tail  + "(\"" + item+"test\");");
			}else{
				System.out.println("item.set" + item.substring(0, 1).toUpperCase() + item.substring(1) + "(\"" + item+"test\");");
			}
		}
	}
	
}
