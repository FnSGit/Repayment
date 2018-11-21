package com.fs.generate.tool;


import com.fs.generate.GenerateObjAndDao;
import com.fs.util.character.StringUtil;
import com.fs.util.db.DataBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GenerateDaosql {
	
	public static void generateDao() {
		
		
		String tableName=GenerateObjAndDao.tableName;
		String condition=GenerateObjAndDao.condition;
		
		
		String objName=StringUtil.lineToHump1(tableName);
		
		StringBuilder daoBuilder=new StringBuilder();
		
		daoBuilder.append("public class ").append(StringUtil.lineToHump1(tableName)).append("Dao {\n").append(System.lineSeparator());
		
		daoBuilder.append("private int retCode=0;"+ System.lineSeparator())
		.append("private String errMsg;"+ System.lineSeparator())
		.append("private int errCode;"+ System.lineSeparator());
		daoBuilder.append("//getter and setter\n"+ System.lineSeparator());

		daoBuilder.append("public String getErrMsg() {").append(System.lineSeparator())
		.append("	return errMsg;").append(System.lineSeparator())
		.append("}\n").append(System.lineSeparator());
		daoBuilder.append("public int getErrCode() {").append(System.lineSeparator())
		.append("	return errCode;").append(System.lineSeparator())
		.append("}\n").append(System.lineSeparator());
		
		List<String> colomns = null;
		try {
			
			colomns = DataBase.getColumns(GenerateObjAndDao.DbPool,tableName);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		List<String> entries=new ArrayList<String>();
		for (String colomn : colomns) {
			entries.add(StringUtil.lineToHump(colomn));
		}
		
		daoBuilder.append(DaoSqlUtil.insertFunction(colomns, entries, tableName, objName));
		daoBuilder.append(DaoSqlUtil.selectFunction(colomns, entries, tableName, objName, condition));
		daoBuilder.append(DaoSqlUtil.updateFunction(colomns, entries, tableName, objName, condition));
		daoBuilder.append(DaoSqlUtil.openCursorFunction(colomns, entries, tableName, objName, condition));
		daoBuilder.append(DaoSqlUtil.fetchCursorFunction(objName));
		daoBuilder.append(DaoSqlUtil.closeCursorFunction());
		daoBuilder.append("}");
		FileOutputStream fileOutputStream;
		try {
			String path="generate/src/com/fs/generate/target/dao/";
			File file=new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			fileOutputStream=new FileOutputStream(path+objName+"Dao"+".java");
			try {
				fileOutputStream.write(daoBuilder.toString().getBytes());
				fileOutputStream.close();
			} catch (IOException e) {
				System.out.println("写文件失败！");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("文件流失败！");
			e.printStackTrace();
		}
		
	}
}
