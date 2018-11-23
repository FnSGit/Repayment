package com.fs.entity;

import com.fs.group.Group;
import com.fs.util.object.ObjectUtil;

import java.lang.reflect.Field;
import java.util.Map;

public class TaskEntity {
    protected Map<String,Field[]> indexs;
    protected Field[] uniqueIndx;
    protected Field[] otherIndx;
    protected String taskName;
    protected String className;
    protected String groupId;
    protected Group group;

    public void initUniqIndx(TaskEntity entity,String... columns) {
        uniqueIndx = new Field[columns.length];
        for (int i=0;i<columns.length;++i) {
            uniqueIndx[i] = ObjectUtil.getField(entity, columns[i]);
        }

    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public Field[] getUniqueIndx() {
        return uniqueIndx;
    }

    public void setUniqueIndx(Field[] uniqueIndx) {
        this.uniqueIndx = uniqueIndx;
    }

    public Map<String, Field[]> getIndexs() {
        return indexs;
    }

    public void setIndexs(Map<String, Field[]> indexs) {
        this.indexs = indexs;
    }

    public Field[] getOtherIndx() {
        return otherIndx;
    }

    public void setOtherIndx(Field[] otherIndx) {
        this.otherIndx = otherIndx;
    }
}
