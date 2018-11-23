package com.fs.busi;

import com.fs.entity.TaskEntity;
import com.fs.group.Group;
import com.fs.util.db.DataBase;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class  BusiProcess {

     public static String dbpool;
     public static Statement statement;

     protected static void dbInit(String db) {
          dbpool=db;
          try {
               statement=DataBase.getConn(db).createStatement();
          } catch (SQLException e) {
               e.printStackTrace();
          }
     }
     public abstract List<TaskEntity> getProcessData(Group param);

     public abstract void process(List<TaskEntity> taskEntityList);


}
