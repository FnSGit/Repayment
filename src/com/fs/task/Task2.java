package com.fs.task;

import com.fs.busi.BusiProcess;
import com.fs.entity.TaskEntity;
import com.fs.repayment.Param.GroupParam;

public class Task2 extends Task {


    public Task2(TaskEntity entity, BusiProcess process, GroupParam param) {
        super(entity, process, param);
    }

    @Override
    public void run() {
        super.run();
      /*  System.out.println(this.taskName+"执行开始。");
        try {
            Thread.sleep(2000);
            System.out.println(this.taskName+"执行结束。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }
}
