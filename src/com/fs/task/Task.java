package com.fs.task;

import com.fs.busi.BusiProcess;

public class Task implements Runnable{
    protected String taskName;
    protected BusiProcess process;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void run() {
        System.out.println(taskName+"开始···");
    }

    protected void end() {
        System.out.println(taskName+"结束···");
    }
}
