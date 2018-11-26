package com.fs.repayment;

import com.fs.busi.repayment.PayPlan;
import com.fs.constants.ConstantComm;
import com.fs.generate.target.entity.YizhiFkxxObj;
import com.fs.generate.target.entity.YizhiHkjihuaObj;
import com.fs.util.db.DataBase;
import com.fs.util.object.ObjectUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Statement statement= null;
        try {
            statement = DataBase.getConn(ConstantComm.repayment_dbpool).createStatement();
            DataBase.setFalseCommit(ConstantComm.repayment_dbpool);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        PayPlan plan = new PayPlan(statement);
        String sql = "select * from yizhi_fkxx where orderno='U1D-5FJ-7HU'";
        ResultSet resultSet=DataBase.getResultset(ConstantComm.repayment_dbpool, sql);
        YizhiFkxxObj fkxx = new YizhiFkxxObj();
        try {
            resultSet.next();
            ObjectUtil.loadResult(fkxx, resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<YizhiHkjihuaObj> lstHkjihua= plan.getPayPlan(fkxx);
        plan.insertPlan(lstHkjihua,fkxx);
    }
}
