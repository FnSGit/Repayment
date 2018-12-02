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
    protected Group group;
    protected TaskVariable variable;

    protected List<Group> groups;
    public Task( BusiProcess process) {
       this.taskName = process.busiName;
       this.process=process;
    }

    public Task(BusiProcess process, Group groups) {
        this.taskName = process.busiName;
        this.process=process;
        this.group = groups;
    }
    @Override
    public void run() {
        //初始化
        FsLogger logger = FsLogger.getLogger(taskName);
        logger.setLogPath("log"+File.separator+ taskName);

       start();
        process.process(process.getProcessData(group));
       end();
    }

    private void end() {
        System.out.println("任务：【"+ taskName+"】  片段id："+group.getGroupId()+"，结束···");
    }

    private void start() {
        System.out.println("任务：【"+ taskName+"】  片段id："+group.getGroupId()+"，开始···");
    }

    public  List<Task> taskFactroy() {
        List<Task> taskList = new ArrayList<>();
//        List<String> groups= param.getGroupIdList();
       /* for (String id:groups){
            param.setGroupId(id);
            Task task = new Task(taskName, process, param);
            taskList.add(task);
        }*/
       for (Group group : groups) {
           taskList.add(new Task( process, group));
       }
        return taskList;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
