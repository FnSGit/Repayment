package com.fs.busi;

import com.fs.Param.Group;
import com.fs.entity.TaskEntity;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.util.db.DataBase;
import com.fs.util.log.FsLogger;
import com.fs.util.object.ObjectUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepaymentBusi extends BusiProcess {


    @Override
    public void process(List<TaskEntity> data) {
        FsLogger logger = FsLogger.getLogger(this.getClass().getName());
        try {
          for (TaskEntity entity:data) {
              YizhiFkxxObj fkxxObj= (YizhiFkxxObj) entity;
              logger.debug("开始执行组id:{},数据:{}" ,fkxxObj.getGroupId(),fkxxObj.getOrderno());

              logger.debug("结束执行组id:{},数据:{}" ,fkxxObj.getGroupId(),fkxxObj.getOrderno());
          }
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<TaskEntity> initGroup(Group param)  {
        List<TaskEntity> taskEntityList = new ArrayList<>();
        String sql = "select * from yizhi_fkxx where plfzuhao ='"+param.getGroupId()+"'";
        ResultSet resultSet = DataBase.getResultset(param.getDbPool(),sql );
        try {
            while (resultSet.next()) {
                TaskEntity taskEntity = (TaskEntity) ObjectUtil.loadResult(YizhiFkxxObj.class,resultSet);
                taskEntity.setGroupId(param.getGroupId());
                taskEntityList.add(taskEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskEntityList;
    }

    private void hkjhProsess(TaskEntity entity) {

    }
}
