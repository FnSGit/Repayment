package com.fs.busi;

import com.fs.entity.TaskEntity;
import com.fs.group.Group;
import com.fs.task.Task;
import com.fs.task.TaskVariable;
import com.fs.util.db.DataBase;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class  BusiProcess {

     public String busiName;
     protected TaskVariable variable;
     protected  String dbpool;
     protected  Statement statement;

     public BusiProcess() {
     }
     public BusiProcess(TaskVariable taskVariable) {
          dbInit(taskVariable.dbpool);
          this.variable=taskVariable;
          busiName=taskVariable.taskName;
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

     public List<Task> taskFactroy(List<Group> groups, TaskVariable taskVariable) {
          List<Task> taskList = new ArrayList<>();
          for (Group group:groups)
               taskList.add(new Task(this, taskVariable));
          return taskList;
     }

}
