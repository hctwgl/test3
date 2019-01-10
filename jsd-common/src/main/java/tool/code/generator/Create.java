package tool.code.generator;

import org.apache.commons.lang.StringUtils;


public class Create {
	public static void main(String[] args) {
		Create ot=new Create();
		ot.test();
	}
	
	public void test(){

	 // 数据库连接信息
		String url = "jdbc:mysql://192.168.112.31:3306/jsd_loan?useUnicode=true&characterEncoding=utf8";
		String MysqlUser = "jsd_user";
		String mysqlPassword = "jsd_Password";

		// 数据库及数据表名称
		String database = "jsd_loan";


		String tables = "jsd_total_info";

		// 配置作者及Domain说明
		String classAuthor = "CodeGenerate";
		String functionName = "";


//		String tables = "af_facescore_img";
//
		// 公共包路径 (例如 BaseDao、 BaseService、 BaseServiceImpl)
		String commonName = "";

		String packageName ="com.ald.fanbei.api";
		String moduleName = "";

		// Mapper文件存储地址 默认在resources中
		String batisName = "config/mapping";
		String db = "mysql";

		try {
		    String[] table = tables.split(",");
		    for (String tableName : table) {
				if (StringUtils.isNotBlank(tableName)) {
				    String classNamePrefix = getClassName(tableName);
				    MybatisGenerate.generateCode(db, url, MysqlUser, mysqlPassword, database, tableName, commonName, packageName, batisName, moduleName, classAuthor, functionName, classNamePrefix);
				}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}
	
	private static String getClassName(String tableName) {
	    String beanName = tableName.substring(0,1).toUpperCase() + tableName.substring(1);
	    while(beanName.indexOf("_") >=0){
	        int firstSpe = beanName.indexOf("_");
	        beanName = beanName.substring(0,firstSpe) + beanName.substring(firstSpe+1,firstSpe+2).toUpperCase() + beanName.substring(firstSpe+2);
	    }
		return beanName;
	}
	

}
