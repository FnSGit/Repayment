package com.fs.group;

import com.fs.dao.repayment.FkxxDao;
import com.fs.util.db.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepaymentGroup extends Group {


    public RepaymentGroup(String groupId, String dbPool, String tableName) {
        super(groupId, dbPool, tableName);
    }

    @Override
    public  List<Group> groupParamBuild() {
//        tableName = "yizhi_fkxx";
        FkxxDao fkxxDao = new FkxxDao(dbPool);
        List<Group> groupList = new ArrayList<>();
//        String sql = "SELECT plfzuhao FROM "+tableName+" GROUP BY plfzuhao;";
        String sql = fkxxDao.fkxx_Group_byPlfzuhao();
        ResultSet resultSet = DataBase.getResultset(dbPool, sql);
        try {
            while (resultSet.next())
                groupList.add(new RepaymentGroup(resultSet.getString(1),dbPool,tableName));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupList;
    }
}
