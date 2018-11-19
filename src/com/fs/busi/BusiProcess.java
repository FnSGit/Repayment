package com.fs.busi;

import com.fs.entity.TaskEntity;
import com.fs.Param.Group;

import java.util.List;

public abstract class  BusiProcess {



     public abstract List<TaskEntity> initGroup(Group param);

     public abstract void process(List<TaskEntity> taskEntityList);


}
