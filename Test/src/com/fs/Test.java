package com.fs;

import com.fs.busi.BusiProcess;
import com.fs.busi.repayment.RepaymentBusi;
import com.fs.constants.ConstantComm;
import com.fs.group.Group;
import com.fs.group.RepaymentGroup;
import com.fs.pool.TaskExecutor;
import com.fs.task.Task;
import com.fs.task.repayment.RepaymentVariable;


public class Test {
    public static void main(String[] args) throws Exception {
       threadTest();
    }

    private static void threadTest() throws Exception {
        TaskExecutor executor = new TaskExecutor(10,TaskExecutor.ExecutorService_fixed);

        BusiProcess process=new RepaymentBusi(new RepaymentVariable("还款计划",ConstantComm.repayment_dbpool));
        Group group = new RepaymentGroup(new RepaymentVariable("还款计划",ConstantComm.repayment_dbpool));

//        DataBase.Prepare();
////        executor.execute(task1);
         executor.batchExecute(group.groupParamBuild());


        executor.shutdown();
    }
}
