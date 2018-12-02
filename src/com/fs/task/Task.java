package com.fs.task;

import com.fs.busi.BusiProcess;
import com.fs.group.Group;
import com.fs.util.log.FsLogger;

import java.io.File;
import java.util.List;

public  class Task implements Runnable{
    protected  String taskName;
    protected BusiProcess process;
    protected Group group;
    protected TaskVariable variable;

    protected List<Group> groups;
    public Task( BusiProcess process,TaskVariable taskVariable) {
       this.taskName = process.busiName;
       this.process=process;
       this.variable=taskVariable;
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


    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
