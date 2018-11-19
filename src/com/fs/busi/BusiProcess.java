package com.fs.busi;

import com.fs.entity.TaskEntity;
import com.fs.repayment.Param.GroupParam;

import java.util.List;

public abstract class  BusiProcess {



     public abstract List<TaskEntity> initGroup(GroupParam param);

     public abstract void process(List<TaskEntity> taskEntityList);


}
