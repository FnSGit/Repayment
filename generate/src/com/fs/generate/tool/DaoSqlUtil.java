package com.fs.generate.tool;

import com.fs.util.character.StringUtil;

import java.util.List;


public class DaoSqlUtil {


	public static String insertFunction(List<String> colomns, List<String> entries, String tableName, String objName) {
		StringBuilder function = new StringBuilder();
		function.append("  public int insert(Database db,").append(objName).append("Obj").append(" obj){" + System.lineSeparator())
				.append("  String sql=\"").append("insert into ").append(tableName).append(" (");
		for (int i = 0; i < colomns.size(); i++) {
			if (i == colomns.size() - 1) {
				function.append(colomns.get(i));
				break;
			}
			function.append(colomns.get(i)).append(",");
		}
		function.append(" ) VALUES ( ");
		for (int i = 0; i < entries.size(); i++) {
			if (i == entries.size() - 1) {
				function.append("$s:"+entries.get(i));
				break;
			}
			function.append("$s:"+entries.get(i)).append(",");
		}
		function.append(")\";" + System.lineSeparator());
		
		function.append("	retCode=db.dbsExecuteSql(sql, obj, obj, CommConstant.DBTIMEOUT);" + System.lineSeparator())
				.append("	if(retCode!=Database.DBS_SUCCESS){" + System.lineSeparator())
				.append("		this.errCode = db.dbsGetErrno();" + System.lineSeparator())
				.append("		this.errMsg = db.dbsGetError();" + System.lineSeparator())
				.append("	}" + System.lineSeparator())
				.append("	return retCode;" + System.lineSeparator())
				.append("  }\n" + System.lineSeparator());

		return function.toString();
	}

	public static String selectFunction(List<String> colomns, List<String> entries, String tableName, String objName, String condition){
		StringBuilder function=new StringBuilder();
		function.append("  public int select(Database db,").append(objName).append("Obj").append(" obj){"+ System.lineSeparator())
		.append("  String sql=\"select ");
		for (int i = 0; i < colomns.size(); i++) {
			
			if (i == colomns.size() - 1) {
				function.append(colomns.get(i));
				break;
			}
			function.append(colomns.get(i)).append(",");
		}
		function.append(" into ");
		for (int i = 0; i < entries.size(); i++) {
			
			if (i == entries.size() - 1) {
				function.append("#s:"+entries.get(i));
				break;
			}
			function.append("#s:"+entries.get(i)).append(",");
		}
		function.append(" from ").append(tableName).append(" where ");
		function.append(condition).append("=$s:").append(StringUtil.lineToHump(condition)).append("\";"+ System.lineSeparator());
		function.append("	retCode=db.dbsExecuteSql(sql, obj, obj, CommConstant.DBTIMEOUT);" + System.lineSeparator())
				.append("	if(retCode!=Database.DBS_SUCCESS){" + System.lineSeparator())
				.append("		this.errCode = db.dbsGetErrno();" + System.lineSeparator())
				.append("		this.errMsg = db.dbsGetError();" + System.lineSeparator())
				.append("	}" + System.lineSeparator())
				.append("  return retCode;" + System.lineSeparator())
				.append("  }\n" + System.lineSeparator());
		return function.toString();
	}

	public static String updateFunction(List<String> colomns, List<String> entries, String tableName, String objName, String condition){
		StringBuilder function=new StringBuilder();
		function.append("  public int update(Database db,").append(objName).append("Obj").append(" obj){"+ System.lineSeparator());
		function.append("  String sql=\"update ").append(tableName).append(" set ");
		for (int i = 0; i < colomns.size(); i++) {
			
			if (i==colomns.size()-1) {
				function.append(colomns.get(i)).append("=$s:").append(entries.get(i));
				break;
			}
			function.append(colomns.get(i)).append("=$s:").append(entries.get(i)).append(",");
		}
		function.append(" where ").append(condition).append("=$s:").append(StringUtil.lineToHump(condition)).append("\";"+ System.lineSeparator());
		
		function.append("	retCode=db.dbsExecuteSql(sql, obj, obj, CommConstant.DBTIMEOUT);" + System.lineSeparator())
				.append("	if(retCode!=Database.DBS_SUCCESS){" + System.lineSeparator())
				.append("		this.errCode = db.dbsGetErrno();" + System.lineSeparator())
				.append("		this.errMsg = db.dbsGetError();" + System.lineSeparator())
				.append("	}" + System.lineSeparator())
				.append("  return retCode;" + System.lineSeparator())
				.append("  }\n" + System.lineSeparator());
		
		return function.toString();
	}
	
	public static String openCursorFunction(List<String> colomns, List<String> entries, String tableName, String objName, String condition){
		StringBuilder function=new StringBuilder();
		function.append("  public SqlSession openCursor(Database db,").append(objName).append("Obj").append(" obj){"+ System.lineSeparator());
		function.append("  String sql=\"select ");
		
		for (int i = 0; i < colomns.size(); i++) {
			
			if (i == colomns.size() - 1) {
				function.append(colomns.get(i));
				break;
			}
			function.append(colomns.get(i)).append(",");
		}
		function.append(" into ");
		for (int i = 0; i < entries.size(); i++) {
			
			if (i == entries.size() - 1) {
				function.append("#s:"+entries.get(i));
				break;
			}
			function.append("#s:"+entries.get(i)).append(",");
		}
		function.append(" from ").append(tableName).append(" where ").append(condition)
		.append("=$s:").append(StringUtil.lineToHump(condition)).append("\";"+ System.lineSeparator());
		
		function.append("	SqlSession sqlSession = db.dbsOpenCursor(sql, obj,CommConstant.DBTIMEOUT);" + System.lineSeparator())
		.append("	if (sqlSession == null) {" + System.lineSeparator())
		.append("		this.errCode = db.dbsGetErrno();" + System.lineSeparator())
		.append("		this.errMsg = db.dbsGetError();" + System.lineSeparator())
		.append("	}" + System.lineSeparator())
		.append("  return sqlSession;" + System.lineSeparator())
		.append("  }\n" + System.lineSeparator());
		return function.toString();
	}
	
	public static String fetchCursorFunction(String objName){
		StringBuilder function=new StringBuilder();
		function.append("  public int fetchCursor(Database db,").append(objName).append("Obj").append(" obj, SqlSession sqlSession){"+ System.lineSeparator())
		.append(" 	retCode = db.dbsFetchCursor(sqlSession, obj);"+ System.lineSeparator())
		.append("	if (retCode != Database.DBS_SUCCESS) {"+ System.lineSeparator())
		.append("		this.errCode = db.dbsGetErrno();"+ System.lineSeparator())
		.append("		this.errMsg = db.dbsGetError();"+ System.lineSeparator())
		.append("	}"+ System.lineSeparator())
		.append("  return retCode;"+ System.lineSeparator())
		.append("  }\n"+ System.lineSeparator());
		return function.toString();
	}
	
	public static String closeCursorFunction(){
		StringBuilder function=new StringBuilder();
		function.append("  public int closeCursor(Database db,SqlSession sqlSession){").append(System.lineSeparator());
		function.append("		return db.dbsCloseCursor(sqlSession);").append(System.lineSeparator());
		function.append("	}\n").append(System.lineSeparator());
		return function.toString();
	}
	
	
	
	
	
	
	
	
}
