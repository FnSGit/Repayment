package com.fs.busi;

import com.fs.entity.TaskEntity;
import com.fs.repayment.Param.GroupParam;

import java.util.List;

public class RepaymentBusi extends BusiProcess {

    private GroupParam param;

    @Override
    public void process(List<TaskEntity> data) {
        try {

            System.out.println("执行组片段id:"+param.getGroupId());
            Thread.sleep(1000);
            System.out.println();
            System.out.println("执行组片段id:"+param.getGroupId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<TaskEntity> initGroup(GroupParam param) {
        this.param=param;

        return null;
    }
}
