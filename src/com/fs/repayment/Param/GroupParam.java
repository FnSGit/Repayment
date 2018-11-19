package com.fs.repayment.Param;

import java.util.List;

public class GroupParam {

    protected String groupId;

    protected List<String> groupIdList;


    public GroupParam(String groupId) {
        this.groupId = groupId;
    }

    public GroupParam(List<String> groupIdList) {
       this.groupIdList=groupIdList;
    }
    public String getGroupId() {
        return groupId;
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
