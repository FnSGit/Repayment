package com.fs.repayment;

import com.fs.busi.BusiProcess;
import com.fs.busi.RepaymentBusi;
import com.fs.entity.TaskEntity;
import com.fs.pool.BatchTaskExecutor;
import com.fs.pool.TaskExecutor;
import com.fs.Param.Group;
import com.fs.task.Task;

import java.util.ArrayList;
import java.util.List;


public class Test {
    public static void main(String[] args) throws Exception {
       threadTest();
    }

    private static void threadTest() throws Exception {
        TaskExecutor executor = new BatchTaskExecutor(10,TaskExecutor.ExecutorService_fixed);
        TaskEntity entity=new TaskEntity("计划生成");
        BusiProcess process=new RepaymentBusi();

        List<String> groups = new ArrayList<>();
        groups.add("11");
        groups.add("20");
        List<Group> param = Group.groupParamBuild(groups);

        Task task = new Task("还款计划",process,param);
////        executor.execute(task1);
        ((BatchTaskExecutor) executor).batchExecute(task.taskFactroy());


        executor.shutdown();
    }
}
