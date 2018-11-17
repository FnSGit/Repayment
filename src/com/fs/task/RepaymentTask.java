package com.fs.task;

import com.fs.busi.BusiProcess;

public class RepaymentTask extends Task {
    
    public RepaymentTask(String taskName,BusiProcess process) {
        super(taskName);
    }

    @Override
    public void run() {
        super.run();
        /******************任务开始*******************/
        process.process();
        /******************任务结束*******************/
        super.end();
    }
}
