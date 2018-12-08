package com.fs.busi;

import com.fs.busi.payplan.PayPlan;
import com.fs.dao.FkxxDao;
import com.fs.entity.TaskEntity;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;
import com.fs.group.Group;
import com.fs.task.TaskVariable;
import com.fs.util.db.DataBase;
import com.fs.util.log.FsLogger;
import com.fs.util.object.ObjectUtil;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepaymentBusi extends BusiProcess {

    public RepaymentBusi(TaskVariable taskVariable) {
        super(taskVariable);
    }

    @Override
    protected void getStatement(String dbpool) {
        try {
            statement=DataBase.getConn(dbpool).createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            statement.setFetchSize(10000);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(List<TaskEntity> data) {
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
              repaymentProcess(fkxxObj);
              logger.debug("结束执行组id:{},数据:{}" ,fkxxObj.getGroupId(),fkxxObj.getOrderno());
          }
          DataBase.commit(dbpool,statement);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    @Override
    public List<TaskEntity> getProcessData(Group param)  {
        List<TaskEntity> taskEntityList = new ArrayList<>();
        FkxxDao fkxxDao = new FkxxDao(dbpool);
//        String sql = "select * from yizhi_fkxx where plfzuhao ='"+param.getGroupId()+"'";
        String sql = fkxxDao.sel_Fkxx_ByGroupId("plfzuhao",param.getGroupId());
        try {
        ResultSet resultSet = DataBase.getResultset(statement,sql );
            while (resultSet.next()) {
                TaskEntity taskEntity = (TaskEntity) ObjectUtil.loadResult(YizhiFkxxObj.class,resultSet);
                taskEntity.setGroupId(param.getGroupId());
                taskEntity.setGroup(param);
                taskEntityList.add(taskEntity);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskEntityList;
    }

    private void repaymentProcess(TaskEntity entity) {
        YizhiFkxxObj fkxx= (YizhiFkxxObj) entity;
        BigDecimal zerBigDecimal=BigDecimal.ZERO;

        PayPlan plan = new PayPlan(dbpool,statement);
        List<YizhiHkjihuaObj> lstHkjihua= plan.getPayPlan(fkxx);
        plan.insertPlan(lstHkjihua,fkxx);
        plan.payDetail(lstHkjihua,fkxx);

    }
}
