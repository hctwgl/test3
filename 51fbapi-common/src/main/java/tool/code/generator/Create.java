package tool.code.generator;


public class Create {
	public static void main(String[] args) {
		Create ot=new Create();
		ot.test();
	}
	
	public void test(){

		// 数据库连接信息
		String url = "jdbc:mysql://192.168.101.85:3306/51fanbei_app?useUnicode=true&characterEncoding=utf8";
		String MysqlUser = "51fanbei";
		String mysqlPassword = "Hello1234";
		
		// 数据库及数据表名称
		String database = "51fanbei_app";
		//String table = "tt_test";
		String table = "af_goods_price";

		
		
		
		// 配置作者及Domain说明
		String classAuthor = "maqiaopan-template";
		//TODO:String classAuthor = " jinhu.chenjh";
		String functionName = "'第三方-上树请求记录";
		
		// 公共包路径 (例如 BaseDao、 BaseService、 BaseServiceImpl)
		String commonName ="";
		
		String packageName ="com.ald.fanbei.api";
		String moduleName = "";

		//Mapper文件存储地址  默认在resources中
		String batisName = "config/mapping";
		String db="mysql";
		
		try {
			String classNamePrefix = getClassName(table);
			MybatisGenerate.generateCode(db,url, MysqlUser, mysqlPassword, database, table,commonName,packageName,batisName,moduleName,classAuthor,functionName,classNamePrefix);
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
