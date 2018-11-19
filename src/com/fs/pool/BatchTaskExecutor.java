package com.fs.pool;

import com.fs.task.Task;

import java.util.List;

public class BatchTaskExecutor extends TaskExecutor{

    public BatchTaskExecutor(int poolSize, int executorService_Type) throws Exception {
        super(poolSize, executorService_Type);
    }

    public void batchExecute(List<Task> taskList) {
        for (Task entity:taskList)
            executorService.execute(entity);
    }
}
