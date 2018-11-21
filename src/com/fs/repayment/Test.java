package com.fs.repayment;

import com.fs.group.Group;
import com.fs.group.RepaymentGroup;
import com.fs.busi.BusiProcess;
import com.fs.busi.repayment.RepaymentBusi;
import com.fs.pool.BatchTaskExecutor;
import com.fs.pool.TaskExecutor;
import com.fs.task.Task;
import com.fs.util.db.DataBase;


public class Test {
    public static void main(String[] args) throws Exception {
       threadTest();
    }

    private static void threadTest() throws Exception {
        TaskExecutor executor = new BatchTaskExecutor(10,TaskExecutor.ExecutorService_fixed);
        BusiProcess process=new RepaymentBusi();
        Group group = new RepaymentGroup();

        DataBase.Prepare();
        Task task = new Task("还款计划",process,group.groupParamBuild());
////        executor.execute(task1);
        ((BatchTaskExecutor) executor).batchExecute(task.taskFactroy());


        executor.shutdown();
    }
}
