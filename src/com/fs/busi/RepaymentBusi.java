package com.fs.busi;

import com.fs.entity.TaskEntity;
import com.fs.Param.Group;
import com.fs.util.log.FsLogger;

import java.util.ArrayList;
import java.util.List;

public class RepaymentBusi extends BusiProcess {


    @Override
    public void process(List<TaskEntity> data) {
        FsLogger logger = FsLogger.getLogger(this.getClass().getName());
        try {
          for (TaskEntity entity:data)
              logger.debug("执行组片段id:"+entity.getGroupId());
            Thread.sleep(1000);
            System.out.println();

            System.out.println("执行组片段id:"+data);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<TaskEntity> initGroup(Group param) {
        List<TaskEntity> taskEntityList = new ArrayList<>();
        for (int i=0;i<2;i++){
            taskEntityList.add(new TaskEntity(param.getGroupId()));
        }
        return taskEntityList;
    }

}
