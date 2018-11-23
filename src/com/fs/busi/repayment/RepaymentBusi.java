package com.fs.busi.repayment;

import com.fs.busi.BusiProcess;
import com.fs.entity.TaskEntity;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;
import com.fs.group.Group;
import com.fs.util.db.DataBase;
import com.fs.util.log.FsLogger;
import com.fs.util.object.ObjectUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepaymentBusi extends BusiProcess {


    @Override
    public void process(List<TaskEntity> data) {
        dbInit("v7yizhi");
        FsLogger logger = FsLogger.getLogger(this.getClass().getName());
        try {
          for (TaskEntity entity:data) {
              YizhiFkxxObj fkxxObj= (YizhiFkxxObj) entity;
              logger.debug("开始执行组id:{},数据:{}" ,fkxxObj.getGroupId(),fkxxObj.getOrderno());
              /*
              data init
               */

              /*
              data process
               */
              hkjhProcess(fkxxObj);
              logger.debug("结束执行组id:{},数据:{}" ,fkxxObj.getGroupId(),fkxxObj.getOrderno());
          }
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public List<TaskEntity> getProcessData(Group param)  {
        List<TaskEntity> taskEntityList = new ArrayList<>();
        String sql = "select * from yizhi_fkxx where plfzuhao ='"+param.getGroupId()+"'";
        ResultSet resultSet = DataBase.getResultset(param.getDbPool(),sql );
        try {
            while (resultSet.next()) {
                TaskEntity taskEntity = (TaskEntity) ObjectUtil.loadResult(YizhiFkxxObj.class,resultSet);
                taskEntity.setGroupId(param.getGroupId());
                taskEntity.setGroup(param);
                taskEntityList.add(taskEntity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskEntityList;
    }

    private void hkjhProcess(TaskEntity entity) {
        YizhiFkxxObj fkxx= (YizhiFkxxObj) entity;
        BigDecimal zerBigDecimal=BigDecimal.ZERO;

        String sOrderNo=fkxx.getOrderno();//订单号
        double dfkje=Double.parseDouble(fkxx.getFkje());
        int hxfs=Integer.parseInt(fkxx.getHxfs());
        int kouxifs=Integer.parseInt(fkxx.getKouxifs());

        initPayParam initPayParam = new initPayParam();
        List<YizhiHkjihuaObj> lstHkjihua= initPayParam.getPayPlan(fkxx);

    }
}
