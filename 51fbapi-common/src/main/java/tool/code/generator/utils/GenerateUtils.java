package tool.code.generator.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;





public class GenerateUtils {
	/**
	 * 获取表信息
	 *
	 * @param table_name
	 * @return
	 */
	public static List<Table> getList(String dbClass, String table_name, String url, String MysqlUser,
			String mysqlPassword, String database, String sql) {
		List<Table> list = new ArrayList<Table>();
		Connection conn = getDblink(dbClass, url, MysqlUser, mysqlPassword);
		Statement stmt = null;
		try {
			stmt = (Statement) conn.createStatement();

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Table table = new Table();
				table.setTypeName(StringUtils.lowerCase(rs.getString("COLUMN_NAME")));
				table.setColumnName(StringUtils.uncapitalize(toUpper(StringUtils.lowerCase(rs.getString("COLUMN_NAME"))))); // 将所有的字段名改为小写方便命名
				table.setColumnNameUpper(StringUtils.capitalize(table.getColumnName()));
				table.setDataType(sqlTypeToJavaType(rs.getString("DATA_TYPE")));
				table.setJdbcType(sqlTypeToJdbcType(rs.getString("DATA_TYPE")));
				table.setColumnComment(rs.getString("COLUMN_COMMENT"));
				list.add(table);
			}
			rs.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}

		return list;
	}

	/**
	 * 连接数据库
	 * 
	 * @param dbClass
	 * @param url
	 * @param MysqlUser
	 * @param mysqlPassword
	 * @return
	 */
	public static Connection getDblink(String dbClass, String url, String MysqlUser, String mysqlPassword) {
		Connection conn = null;
		try {
			Class.forName(dbClass); // 加载驱动
			conn = DriverManager.getConnection(url, MysqlUser, mysqlPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * "_" + 小写 转成大写字母
	 * 
	 * @param str
	 * @return
	 */
	public static String toUpper(String str) {
		String name = null;
		String[] split = StringUtils.split(str, "_");
		for (String string : split) {
			if (name == null)
				name = StringUtils.capitalize(string);
			else
				name += StringUtils.capitalize(string);
		}
		return name;
	}

	/**
	 * 功能：获得列的数据类型
	 *
	 * @param sqlType
	 * @return
	 */
	private static String sqlTypeToJavaType(String sqlType) {

		if (sqlType.equalsIgnoreCase("bit")) {
			return "Boolean";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			return "Integer";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			return "short";
		} else if (sqlType.equalsIgnoreCase("int")) {
			return "Integer";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "Long";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "Float";
		} else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney") || sqlType.equalsIgnoreCase("double")) {
			return "BigDecimal";
		} else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text")) {
			return "String";
		} else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date")) {
			return "Date";
		} else if (sqlType.equalsIgnoreCase("image")) {
			return "Blod";
		}
		return "String";
	}

	
	
	/**
	 * 功能：获得列的数据类型
	 *
	 * @param sqlType
	 * @return
	 */
	private static String sqlTypeToJdbcType(String sqlType) {

		if (sqlType.equalsIgnoreCase("bit")) {
			return "BIT";
		} else if (sqlType.equalsIgnoreCase("tinyint")) {
			return "INTEGER";
		} else if (sqlType.equalsIgnoreCase("smallint")) {
			return "INTEGER";
		} else if (sqlType.equalsIgnoreCase("int")) {
			return "INTEGER";
		} else if (sqlType.equalsIgnoreCase("bigint")) {
			return "INTEGER";
		} else if (sqlType.equalsIgnoreCase("float")) {
			return "DECIMAL";
		} else if (sqlType.equalsIgnoreCase("decimal") || sqlType.equalsIgnoreCase("numeric")
				|| sqlType.equalsIgnoreCase("real") || sqlType.equalsIgnoreCase("money")
				|| sqlType.equalsIgnoreCase("smallmoney") || sqlType.equalsIgnoreCase("double")) {
			return "DECIMAL";
		} else if (sqlType.equalsIgnoreCase("varchar") || sqlType.equalsIgnoreCase("char")
				|| sqlType.equalsIgnoreCase("nvarchar") || sqlType.equalsIgnoreCase("nchar")
				|| sqlType.equalsIgnoreCase("text")) {
			return "VARCHAR";
		} else if (sqlType.equalsIgnoreCase("datetime") || sqlType.equalsIgnoreCase("date") || sqlType.equalsIgnoreCase("timestamp")) {
			return "TIMESTAMP";
		}
		
		return "VARCHAR";
	}
	/**
	 * 将内容写入文件
	 *
	 * @param content
	 * @param filePath
	 */
	public static void writeFile(String content, String filePath) {
		try {
			if (createFile(filePath)) {
				FileWriter fileWriter = new FileWriter(filePath, true);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(content);
				bufferedWriter.close();
				fileWriter.close();
			} else {
				System.err.println("生成失败，文件已存在！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建单个文件
	 *
	 * @param descFileName
	 *            文件名，包含路径
	 * @return 如果创建成功，则返回true，否则返回false
	 */
	public static boolean createFile(String descFileName) {
		File file = new File(descFileName);
		if (file.exists()) {
			System.err.println("文件 " + descFileName + " 已存在!");
			return false;
		}
		if (descFileName.endsWith(File.separator)) {
			System.err.println(descFileName + " 为目录，不能创建目录!");
			return false;
		}
		if (!file.getParentFile().exists()) {
			// 如果文件所在的目录不存在，则创建目录
			if (!file.getParentFile().mkdirs()) {
				System.err.println("创建文件所在的目录失败!");
				return false;
			}
		}

		// 创建文件
		try {
			if (file.createNewFile()) {
				System.err.println(descFileName + " 文件创建成功!");
				return true;
			} else {
				System.err.println(descFileName + " 文件创建失败!");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(descFileName + " 文件创建失败!");
			return false;
		}
	}

	public static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new java.util.Date());
	}
}