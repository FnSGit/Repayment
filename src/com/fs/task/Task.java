package com.fs.task;

import com.fs.busi.BusiProcess;
import com.fs.entity.TaskEntity;
import com.fs.repayment.Param.GroupParam;
import com.fs.util.log.FsLogger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Task implements Runnable{
    protected  TaskEntity entity;
    protected BusiProcess process;
    protected GroupParam param;

    public Task(TaskEntity entity, BusiProcess process, GroupParam param) {
       this.entity=entity;
       this.process=process;
       this.param=param;
    }

    @Override
    public void run() {
        //初始化
        FsLogger logger = FsLogger.getLogger(entity.getClassName());
        logger.setLogPath("log"+File.separator+entity.getTaskName());

       start();
        process.process(process.initGroup(param));
       end();
    }

    private void end() {
        System.out.println("任务：【"+entity.getTaskName()+"】  片段id："+param.getGroupId()+"，结束···");
    }

    private void start() {
        System.out.println("任务：【"+entity.getTaskName()+"】  片段id："+param.getGroupId()+"，开始···");
    }

    public  List<Task> taskFactroy() {
        List<Task> taskList = new ArrayList<>();
        List<String> groups= param.getGroupIdList();
        for (String id:groups){

        }

        return taskList;
    }
}
