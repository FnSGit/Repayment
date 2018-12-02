package com.fs;

import com.fs.busi.BusiProcess;
import com.fs.busi.RepaymentBusi;
import com.fs.constants.ConstantComm;
import com.fs.group.Group;
import com.fs.group.RepaymentGroup;
import com.fs.pool.TaskExecutor;
import com.fs.task.RepaymentVariable;
import com.fs.task.TaskVariable;


public class Test {
    public static void main(String[] args) throws Exception {
       threadTest();
    }

    private static void threadTest() throws Exception {
        TaskExecutor executor = new TaskExecutor(10,TaskExecutor.ExecutorService_fixed);

        TaskVariable variable=new RepaymentVariable("还款计划",ConstantComm.repayment_dbpool);
        BusiProcess process=new RepaymentBusi(variable);
        Group group = new RepaymentGroup(ConstantComm.repayment_dbpool);

//        DataBase.Prepare();
////        executor.execute(task1);
         executor.batchExecute(group.groupParamBuild(),process,variable);


        executor.shutdown();
    }
}
