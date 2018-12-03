package com.fs.dao;

import com.fs.generate.target.entity.YizhiHolidayObj;
import com.fs.util.object.ObjectUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HolidayDao extends Dao {


    public HolidayDao(String dbpool) {
        super(dbpool);
    }

    public HolidayDao(String dbpool, Statement statement) {
        super(dbpool, statement);
    }

    @Override
    protected void createStatement() {
        defaultStatement();
    }

    public YizhiHolidayObj sel_holiday(String holiday) {
        YizhiHolidayObj yizhiHolidayObj=new YizhiHolidayObj();
        String sql = "select holiday from yizhi_holiday where holidy='" + holiday + "'";
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next())
                ObjectUtil.loadResult(yizhiHolidayObj,resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return yizhiHolidayObj;
    }
}
