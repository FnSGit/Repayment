package com.fs.group;

import com.fs.dao.FkxxDao;
import com.fs.util.db.DataBase;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepaymentGroup extends Group {


    public RepaymentGroup(String group, String dbpool, String table) {
       groupId=group;
       dbPool=dbpool;
       tableName=table;
    }
    public RepaymentGroup( String dbpool) {
       dbPool=dbpool;
       defaultStatement();
    }
    @Override
    public  List<Group> groupParamBuild() {
        FkxxDao fkxxDao = new FkxxDao(dbPool);
        List<Group> groupList = new ArrayList<>();
        String sql = fkxxDao.fkxx_Group_byPlfzuhao();
        try {
        ResultSet resultSet = DataBase.getResultset(statement, sql);
            while (resultSet.next())
                groupList.add(new RepaymentGroup(resultSet.getString(1),dbPool,tableName));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groupList;
    }
}
