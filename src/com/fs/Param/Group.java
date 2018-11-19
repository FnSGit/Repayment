package com.fs.Param;

import java.util.ArrayList;
import java.util.List;

public class Group {

    protected String groupId;

    protected List<String> groupIdList;


    public Group(String groupId) {
        this.groupId = groupId;
    }

    public static List<Group> groupParamBuild(List<String> groupIdList) {
        List<Group> groupList = new ArrayList<>();
        for (String id:groupIdList)
            groupList.add(new Group(id));
        return groupList;
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
