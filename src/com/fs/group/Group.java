package com.fs.group;

import java.util.List;

public abstract class Group {

    protected String groupId;
    protected String groupName;
    protected String dbPool;
    protected String tableName;

    protected List<String> groupIdList;


    public Group(String groupId, String dbPool, String tableName) {
        this.groupId = groupId;
        this.dbPool = dbPool;
        this.tableName = tableName;
    }

    public Group() {
    }

    public abstract  List<Group> groupParamBuild() ;

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDbPool() {
        return dbPool;
    }

    public void setDbPool(String dbPool) {
        this.dbPool = dbPool;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getGroupIdList() {
        return groupIdList;
    }

    public void setGroupIdList(List<String> groupIdList) {
        this.groupIdList = groupIdList;
    }
}
