package com.fs.task;

import com.fs.busi.BusiProcess;
import com.fs.group.Group;
import com.fs.util.log.FsLogger;

import java.io.File;

public  class Task implements Runnable{
    protected  String taskName;
    protected BusiProcess process;
    protected Group group;
    protected TaskVariable variable;


    public Task(BusiProcess process,Group group) {
        this.process = process;
        this.taskName = process.busiName;
        this.variable=process.getVariable();
        this.group=group;
    }

    @Override
    public void run() {
        /*
        初始化
         */
        FsLogger logger = FsLogger.getLogger(taskName);
        logger.setLogPath("log"+File.separator+ taskName);
        /*
        任务开始
         */
       start();
        process.process(process.getProcessData(group));
       end();
       /*
       任务结束
        */
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
