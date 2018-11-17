package com.fs.task;

public class Task2 extends Task {
    public Task2(String taskName) {
        super(taskName);
    }

    @Override
    public void run() {
        super.run();
        System.out.println(this.taskName+"执行开始。");
        try {
            Thread.sleep(2000);
            System.out.println(this.taskName+"执行结束。");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
