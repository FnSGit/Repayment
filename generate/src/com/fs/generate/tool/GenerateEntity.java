package com.fs.generate.tool;


import com.fs.generate.GenerateObjAndDao;
import com.fs.util.character.StringUtil;
import com.fs.util.db.DataBase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GenerateEntity {

	
	public static void generateObj() {
		String tableName=GenerateObjAndDao.tableName;
		
		List<String> colomnList=new ArrayList<String>();
		StringBuilder stringBuilder=new StringBuilder();
		try {
			colomnList=DataBase.getColumns(GenerateObjAndDao.DbPool,tableName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		stringBuilder.append("public class ").append(StringUtil.lineToHump1(tableName.toLowerCase())).append("Obj  {").append(System.lineSeparator());
		for (String colomn : colomnList) {
			stringBuilder.append("	private String " + StringUtil.lineToHump(colomn) + ";").append(System.lineSeparator());

		}
		
		for (String colomn : colomnList) {
			stringBuilder.append(genGetAndSet(colomn)).append(System.lineSeparator());
		}
		
		stringBuilder.append("}");
		
		//System.out.println(stringBuilder.toString());
		FileWriter writer = null;
		try {
			String path="generate/src/com/fs/generate/target/entity/";
			File file=new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			 writer=new FileWriter(path+StringUtil.lineToHump1(tableName)+"Obj"+".java");
		} catch (IOException e) {
			System.out.println("写文件流失败！");
			//e.printStackTrace();
		}
		
		try {
			writer.write(stringBuilder.toString());
			writer.close();
		} catch (IOException e) {
			System.out.println("输出文件失败！");
			//e.printStackTrace();
		}
		
		
		
		
		
		

		
		
		
		
		
		
	}
	public static String genGetAndSet(String str){
 		String method = StringUtil.lineToHump1(str);
 		str=StringUtil.lineToHump(str);
		StringBuffer sb = new StringBuffer();
		sb.append("	public void set").append(method).append("(String ").append(str).append("){\n");
		sb.append("	   this.").append(str).append(" = ").append(str).append(";\n");
		sb.append("	}\n");
		sb.append("	public String get").append(method).append("(){\n");
		sb.append("	   return ").append(str).append(";\n");
		sb.append("	}\n");
		return sb.toString();
 	}
}
