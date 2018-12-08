package com.fs.pool;

import com.fs.busi.BusiProcess;
import com.fs.group.Group;
import com.fs.task.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class TaskExecutor {
    public static final int ExecutorService_fixed=1;
    public static final int ExecutorService_single=2;
    public static final int ExecutorService_cached=3;
    public static final int ExecutorService_scheduled=4;
    protected ExecutorService executorService;
    public TaskExecutor(int poolSize, int executorService_Type) throws Exception {
        switch (executorService_Type) {
            case ExecutorService_scheduled:
                this.executorService = new ScheduledThreadPoolExecutor(poolSize);
                break;
            case ExecutorService_fixed:
                this.executorService = Executors.newFixedThreadPool(poolSize);
                break;
            case ExecutorService_single:
                this.executorService=Executors.newSingleThreadExecutor();
                break;
            case ExecutorService_cached:
                this.executorService=Executors.newCachedThreadPool();
                break;
            default:
                throw new Exception("构建executorService类型错误！");
        }
    }


    public void execute(Task data) {

        executorService.execute(data);

    }
    public void batchExecute(List<Task> taskList) {
        for (Task task:taskList)
            executorService.execute(task);
    }

    public void batchExecute(List<Group> groupList, BusiProcess process) {
        for (Group group : groupList) {
            Task task = new Task(process,group);
            executorService.execute(task);
        }
    }
    public void shutdown() {
        this.executorService.shutdown();
    }
}
