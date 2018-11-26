package com.fs.busi;

import com.fs.entity.TaskEntity;
import com.fs.group.Group;
import com.fs.util.db.DataBase;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class  BusiProcess {



     protected  String dbpool;
     protected  Statement statement;

     public BusiProcess() {
     }
     public BusiProcess(String dbpool) {
          dbInit(dbpool);
     }
     protected  void dbInit(String db) {
          DataBase.setFalseCommit(db);
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
