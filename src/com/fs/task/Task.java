package com.fs.task;

import com.fs.busi.BusiProcess;
import com.fs.group.Group;
import com.fs.util.log.FsLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Task implements Runnable{
    protected  String taskName;
    protected BusiProcess process;
    protected Group param;

    protected List<Group> groups;
    public Task(String taskName, BusiProcess process, Group param) {
       this.taskName = taskName;
       this.process=process;
       this.param=param;
    }

    public Task(String taskName, BusiProcess process, List<Group> groups) {
        this.taskName = taskName;
        this.process=process;
        this.groups = groups;
    }
    @Override
    public void run() {
        //初始化
        FsLogger logger = FsLogger.getLogger(taskName);
        logger.setLogPath("log"+File.separator+ taskName);

       start();
        process.process(process.getProcessData(param));
       end();
    }

    private void end() {
        System.out.println("任务：【"+ taskName+"】  片段id："+param.getGroupId()+"，结束···");
    }

    private void start() {
        System.out.println("任务：【"+ taskName+"】  片段id："+param.getGroupId()+"，开始···");
    }

    public  List<Task> taskFactroy() {
        List<Task> taskList = new ArrayList<>();
//        List<String> groups= param.getGroupIdList();
       /* for (String id:groups){
            param.setGroupId(id);
            Task task = new Task(taskName, process, param);
            taskList.add(task);
        }*/
       for (Group group : groups)
           taskList.add(new Task(taskName, process, group));
        return taskList;
    }
}
