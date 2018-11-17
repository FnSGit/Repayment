package com.fs.repayment;

import com.fs.pool.TaskExecutor;
import com.fs.task.Task;
import com.fs.task.Task1;
import com.fs.task.Task2;
import com.fs.util.date.DateUtil;


public class Test {
    public static void main(String[] args) throws Exception {
        String nextMouth=DateUtil.getNextMonth("20180131",1);
        System.out.println(nextMouth);
        System.out.println(DateUtil.getNextDate(nextMouth,-1));
    }

    private static void threadTest() throws Exception {
        TaskExecutor executor = new TaskExecutor(10,TaskExecutor.ExecutorService_fixed);
        Task task1 = new Task1("任务1");
        executor.execute(task1);
        Task task2 = new Task2("任务2");
        executor.execute(task2);

        executor.shutdown();
    }
}
