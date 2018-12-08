package com.fs.task;

public abstract class TaskVariable {

    public String taskName;
    public String dbpool;

    public TaskVariable(String taskName, String dbpool) {
        this.taskName = taskName;
        this.dbpool = dbpool;
    }

}
